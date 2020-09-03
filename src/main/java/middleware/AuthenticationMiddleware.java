package {{.Package}}.middleware;

import com.mitchdennett.framework.drivers.Session;
import com.mitchdennett.framework.http.MiddlewareChain;
import com.mitchdennett.framework.http.Request;
import com.mitchdennett.framework.http.Response;
import com.mitchdennett.framework.middleware.Middleware;

import javax.inject.Inject;

public class AuthenticationMiddleware implements Middleware {

    @Inject
    private Session session;

    @Override
    public void handle(Request request, Response response, MiddlewareChain middlewareChain) throws Exception {
        if(session.get("userid") == null) {
            response.redirect("/login");
        } else {
            middlewareChain.next();
        }
    }
}
