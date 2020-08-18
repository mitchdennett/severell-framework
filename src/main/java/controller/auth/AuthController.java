package {{.Package}}.controller.auth;

import {{.Package}}.auth.Auth;
import com.mitchdennett.framework.crypto.PasswordUtils;
import com.mitchdennett.framework.drivers.Session;
import com.mitchdennett.framework.http.Request;
import com.mitchdennett.framework.http.Response;
import {{.Package}}.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AuthController {

	public static void register(Request request, Response resp) throws IOException {
        resp.view("auth/register.mustache", new HashMap<String, Object>());
    }

    public static void login(Request request, Response resp, Session session, Auth auth) throws IOException {
        resp.view("auth/login.mustache", new HashMap<String, Object>());
    }

    public static void loginPost(Request req, Auth auth, Response resp) throws IOException {
        if(auth.login(req.input("email"), req.input("password"))){
            resp.redirect("/");
        } else {
            resp.redirect("/login");
        }
    }

    public static void registerPost(Request req, Auth auth, Response resp) throws IOException {
        System.out.println();
        System.out.println(PasswordUtils.hashPassword(req.input("password")));

        User user = new User();
        user.set("email", req.input("email"));
        user.set("password", PasswordUtils.hashPassword(req.input("password")));
        user.set("name", req.input("email"));
        user.saveIt();

        auth.login(req.input("email"), req.input("password"));

        resp.redirect("/");
    }
	
}