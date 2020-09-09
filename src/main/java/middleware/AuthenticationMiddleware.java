package {{.Package}}.middleware;

import com.mitchdennett.framework.drivers.Session;
import com.mitchdennett.framework.http.MiddlewareChain;
import com.mitchdennett.framework.http.Request;
import com.mitchdennett.framework.http.Response;
import com.mitchdennett.framework.middleware.Middleware;

public class AuthenticationMiddleware implements Middleware {

    private Session session;

    public AuthenticationMiddleware(Session session) {
        this.session = session;
    }

    @Override
    public void handle(Request request, Response response, MiddlewareChain middlewareChain) throws Exception {
        if(session.get("userid") == null) {
            response.redirect("/login");
        } else {
            middlewareChain.next();
        }
    }
}
