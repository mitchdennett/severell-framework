package {{.Package}}.middleware;

import com.mitchdennett.framework.annotations.Before;
import com.mitchdennett.framework.drivers.Session;
import com.mitchdennett.framework.http.Response;

import java.io.IOException;

public class AuthenticationMiddleware {

    @Before
    public void before(Session session, Response resp) throws IOException {
        if(session.get("userid") == null) {
            resp.redirect("/login");
        }
    }
}
