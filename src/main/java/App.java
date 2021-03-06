package {{.Package}};

import {{.Package}}.auth.Auth;
import com.severell.core.http.AppServer;
import com.severell.core.config.Config;
import com.severell.core.container.Container;
import com.severell.core.http.Router;
import com.severell.core.providers.ServiceProvider;

import javax.naming.NamingException;
import java.util.ArrayList;


public class App {

    public static void main(String[] args) throws NamingException {
        try {
            Config.loadConfig();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Container c = new Container();
        c.singleton("_MiddlewareList", Middleware.MIDDLEWARE);
        c.singleton(Auth.class, new Auth());

        AppServer server = new AppServer(Config.get("PORT", "8080"));
        c.singleton(AppServer.class, server);

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
            RouteBuilder builder = new RouteBuilder();
            ArrayList routes = builder.build();
            Router.setCompiledRoutes(routes);
            c.singleton("DefaultMiddleware", builder.buildDefaultMiddleware());
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


        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
