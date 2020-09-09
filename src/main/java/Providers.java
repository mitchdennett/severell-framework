package {{.Package}};

import com.mitchdennett.framework.providers.*;

public class Providers {

    public static final Class<ServiceProvider>[] PROVIDERS = new Class[]{
            AppProvider.class,
            SessionProvider.class,
            MailProvider.class,
            RouteProvider.class
    };
}
