package {{.Package}}.main;

import com.mitchdennett.framework.middleware.CsrfMiddleware;
import com.mitchdennett.framework.middleware.SecureHeadersMiddleware;

public class Middleware {

    public static final Class[] MIDDLEWARE = new Class[]{
            CsrfMiddleware.class,
            SecureHeadersMiddleware.class,
    };
}
