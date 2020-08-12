package {{.Package}}.main;

import com.mitchdennett.framework.providers.*;

public class Providers {

    public static final Class<ServiceProvider>[] PROVIDERS = new Class[]{
            AppProvider.class,
            MiddlewareProvider.class,
            RouteProvider.class,
            SessionProvider.class,
            MailProvider.class
    };
}
