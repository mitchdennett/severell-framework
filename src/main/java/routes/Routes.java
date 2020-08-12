package {{.Package}}.main;

import com.mitchdennett.framework.http.Router;
import {{.Package}}.middleware.AuthenticationMiddleware;

public class Routes {

    public static void init() throws NoSuchMethodException, ClassNotFoundException {
        Router.Get("/", "com.mitchdennett.controller.MainController::index").middleware(AuthenticationMiddleware.class);
        Router.Get("/blog/:id", "com.mitchdennett.controller.MainController::blog");
        Router.Get("/register", "com.mitchdennett.controller.MainController::register");
        Router.Get("/login", "com.mitchdennett.controller.MainController::login");
        Router.Post("/login", "com.mitchdennett.controller.MainController::loginPost");
        Router.Post("/register", "com.mitchdennett.controller.MainController::registerPost");
    }
}
