package com.mitchdennett.main;

import com.mitchdennett.framework.config.Config;
import com.mitchdennett.framework.container.Container;

import com.mitchdennett.framework.providers.ServiceProvider;
import org.eclipse.jetty.server.Server;
import org.javalite.activejdbc.Base;


public class Main {

    public static void main(String[] args) {
        try {
            Config.loadConfig();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Container c = new Container();

        Server server = new Server(8080);
        c.bind(server);

        for(Class p : Providers.PROVIDERS) {
            try {
                ServiceProvider provider = (ServiceProvider) p.getDeclaredConstructor(Container.class).newInstance(c);
                provider.register();
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        Base.open(Config.get("DB_DRIVER"),Config.get("DB_CONNSTRING"), Config.get("DB_USERNAME"),Config.get("DB_PASSWORD"));

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
