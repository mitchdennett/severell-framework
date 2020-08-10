package{{.Package}};

import com.mitchdennett.framework.providers.AppProvider;
import com.mitchdennett.framework.providers.MailProvider;
import com.mitchdennett.framework.providers.RouteProvider;
import com.mitchdennett.framework.providers.ServiceProvider;

public class Providers {

    public static final Class<ServiceProvider>[] PROVIDERS = new Class[]{
            AppProvider.class,
            RouteProvider.class,
            MailProvider.class
    };
}
