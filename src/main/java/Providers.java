package {{.Package}};

import com.severell.core.providers.*;

public class Providers {

    public static final Class<ServiceProvider>[] PROVIDERS = new Class[]{
            AppProvider.class,
            JettyProvider.class,
            SessionProvider.class,
            MailProvider.class,
            RouteProvider.class
    };
}
