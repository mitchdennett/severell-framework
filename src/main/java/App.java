package {{.Package}};

import {{.Package}}.auth.Auth;
import {{.Package}}.routes.Routes;
import com.mitchdennett.framework.config.Config;
import com.mitchdennett.framework.container.Container;
import com.mitchdennett.framework.providers.ServiceProvider;
import org.eclipse.jetty.server.Server;

import javax.naming.NamingException;


public class App {

    public static void main(String[] args) throws NamingException {
        {
            try {
                Config.loadConfig();
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            Container c = new Container();
            c.bind("_MiddlewareList", Middleware.MIDDLEWARE);
            c.bind(new Auth());
    
            Server server = new Server(Integer.parseInt(Config.get("PORT")));
            c.bind(server);
    
            try {
                Routes.init();
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
    
            for(Class p : Providers.PROVIDERS) {
                try {
                    ServiceProvider provider = (ServiceProvider) p.getDeclaredConstructor(Container.class).newInstance(c);
                    provider.register();
                }catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
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
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
