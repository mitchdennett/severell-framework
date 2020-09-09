package {{.Package}}.internal;

import com.severell.core.config.Config;
import com.severell.core.container.Container;
import com.severell.core.http.*;
import com.severell.core.providers.ServiceProvider;
import {{.Package}}.Middleware;
import {{.Package}}.Providers;
import {{.Package}}.auth.Auth;
import {{.Package}}.commands.Commander;
import {{.Package}}.routes.Routes;
import com.squareup.javapoet.*;
import org.apache.maven.shared.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AOTCompilation {

    public static Container bootstrap() {
        try {
            Config.loadConfig();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Container c = new Container();
        c.singleton("_MiddlewareList", Middleware.MIDDLEWARE);
        c.singleton(Auth.class, new Auth());

        for(Class p : Providers.PROVIDERS) {
            try {
                ServiceProvider provider = (ServiceProvider) p.getDeclaredConstructor(Container.class).newInstance(c);
                provider.register();
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        try {
            Routes.init();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        for(Class p : Providers.PROVIDERS) {
            try {
                ServiceProvider provider = (ServiceProvider) p.getDeclaredConstructor(Container.class).newInstance(c);
                provider.boot();
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return c;
    }

    public static void main(String[] args) throws Exception {
        Container c = bootstrap();


        Router router = new Router();
        ArrayList<Route> routes = router.getRoutes();

        MethodSpec.Builder builder = getBuildMethodBuilder(c, routes);

        //We now need to compile the default middleware. This middleware gets executed on every route

        MethodSpec.Builder middlewareBuilder = MethodSpec.methodBuilder("buildDefaultMiddleware");
        middlewareBuilder.addModifiers(Modifier.PUBLIC);
        TypeName listOfMethodExecutor = ParameterizedTypeName.get(ArrayList.class, MiddlewareExecutor.class);
        middlewareBuilder.returns(listOfMethodExecutor);
        Class[] middleware = c.make("_MiddlewareList", Class[].class);
        buildMiddleware(c, middlewareBuilder, new ArrayList<Class>(Arrays.asList(middleware)), "defaultMiddleware");
        middlewareBuilder.addStatement("return defaultMiddleware");


        TypeSpec helloWorld = TypeSpec.classBuilder("RouteBuilder")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(builder.build())
                .addMethod(middlewareBuilder.build())
                .build();

        String packageName = Commander.class.getPackage().getName().replace(".commands", "");
        String fileLocation = packageName.replaceAll("\\.", "/");

        JavaFile javaFile = JavaFile.builder(packageName, helloWorld)
                .build();

        Path sourceFile   = Files.createTempDirectory("severell");
        File source = sourceFile.toFile();

        try {
            javaFile.writeTo(source);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create migration");
        }
        File src = new File(sourceFile.toString() + "/" + fileLocation + "/RouteBuilder.java");

        JavaCompiler compiler    = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(null, null, null);

        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(Paths.get("target/classes").toFile()));

        List<String> optionList = new ArrayList<String>();
        optionList.add("-classpath");
		optionList.add(getClassPath());
		optionList.add("-source");
        optionList.add("1.8");
        optionList.add("-target");
        optionList.add("1.8");
        // Compile the file
        compiler.getTask(null,
                fileManager,
                null,
                optionList,
                null,
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(src)))
                .call();
        fileManager.close();

        recursiveDeleteOnExit(sourceFile);

    }

    public static String getClassPath() {
        String cp = "";
        ClassLoader sys = ClassLoader.getSystemClassLoader();
        ClassLoader cl = AOTCompilation.class.getClassLoader();
        for (; cl != null & cl != sys; cl = cl.getParent())
            if (cl instanceof java.net.URLClassLoader) {
                java.net.URLClassLoader ucl = (java.net.URLClassLoader) cl;
                for (java.net.URL url : ucl.getURLs())
                    cp += File.pathSeparator + url.getPath();
            }
        return cp.length()==0 ? null : cp.substring(1);
    }

    public static void recursiveDeleteOnExit(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                                             @SuppressWarnings("unused") BasicFileAttributes attrs) {
                file.toFile().deleteOnExit();
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     @SuppressWarnings("unused") BasicFileAttributes attrs) {
                dir.toFile().deleteOnExit();
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @NotNull
    private static MethodSpec.Builder getBuildMethodBuilder(Container c, ArrayList<Route> routes) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("build");
        builder.addModifiers(Modifier.PUBLIC);
        TypeName listOfRouteExecutor = ParameterizedTypeName.get(ArrayList.class, RouteExecutor.class);
        builder.returns(listOfRouteExecutor);

        builder.addStatement("$T<$T> list = new $T<>()", ArrayList.class, RouteExecutor.class, ArrayList.class);
        int routeIndex = 0;
        for(Route r : routes) {
            String middlwareListName = "middlewareList" + routeIndex;
            builder.addCode("\n // ********* ROUTE: $S - $S ********* \n", r.getHttpMethod(), r.getPath());
            CodeBlock.Builder LambdaBuilder = CodeBlock.builder()
                    .add("(request, response, container) -> {\n").indent()
                    .addStatement("$T cont = new $T()", r.getMethod().getDeclaringClass(),r.getMethod().getDeclaringClass());
            ArrayList<String> paramList = new ArrayList<String>();

            Class[] params = r.getMethod().getParameterTypes();
            resolve(c, LambdaBuilder, paramList, params);

            CodeBlock Lambda = LambdaBuilder.addStatement("cont.$L($L)", r.getMethod().getName(), StringUtils.join(paramList.iterator(), ","))
                    .unindent().add("}")
                    .build();

            //We need to instantiate and resolve middleware here.
            buildMiddleware(c, builder, r.getMiddlewareClassList(), middlwareListName);

            builder.addStatement("list.add(new $T($S, $S, $L, $L))", RouteExecutor.class, r.getPath(), r.getHttpMethod(), middlwareListName, Lambda.toString());
            routeIndex++;
        }

        builder.addStatement("return list");
        return builder;
    }

    private static void buildMiddleware(Container c, MethodSpec.Builder builder, ArrayList<Class> classList, String middlwareListName) {
        builder.addStatement("$T<$T> " + middlwareListName + " = new $T<>()",ArrayList.class, MiddlewareExecutor.class, ArrayList.class);
        if(classList != null) {
            for (Class mid : classList) {
                CodeBlock.Builder middlewareBuilder = CodeBlock.builder();
                Constructor constr = mid.getConstructors()[0];
                middlewareBuilder
                        .add("(request, response, container, chain) -> {\n").indent();
                Class[] parameters = constr.getParameterTypes();
                ArrayList<String> middlwareParamList = new ArrayList<>();
                resolve(c, middlewareBuilder, middlwareParamList, parameters);
                middlewareBuilder.addStatement("$T middleware = new $T($L)", mid, mid, StringUtils.join(middlwareParamList.iterator(), ","));
                middlewareBuilder.addStatement("middleware.handle(request, response, chain)");
                builder.addStatement(middlwareListName + ".add(new $T($L))", MiddlewareExecutor.class, middlewareBuilder.unindent().add("}").build().toString());
            }

        }
    }

    private static void resolve(Container c, CodeBlock.Builder lambdaBuilder, ArrayList<String> paramList, Class[] params) {
        int count = 0;
        for(Class p : params) {
            if (p == Request.class) {
                paramList.add("request");
            } else if (p == Response.class){
                paramList.add("response");
            } else {
                Object obj = c.make(p);
                if(obj instanceof NeedsRequest) {
                    lambdaBuilder.addStatement("$T p" + count + " = container.make($L)", p, p.getName() + ".class");
                    lambdaBuilder.addStatement("(($T) p" + count + ").setRequest(request)", NeedsRequest.class);
                } else {
                    lambdaBuilder.addStatement("$T p" + count + " = container.make($L)", p, p.getName() + ".class");
                }

                paramList.add("p" + count);
            }
            count++;
        }
    }
}
