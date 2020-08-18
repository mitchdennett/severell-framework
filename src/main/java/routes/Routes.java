package {{.Package}}.main;

import com.mitchdennett.framework.http.Router;

public class Routes {

    public static void init() throws NoSuchMethodException, ClassNotFoundException {
        Router.Get("/", "com.mitchdennett.controller.MainController::index");
        Router.Get("/register", "com.mitchdennett.controller.auth.AuthController::register");
        Router.Get("/login", "com.mitchdennett.controller.auth.AuthController::login");
        Router.Post("/login", "com.mitchdennett.controller.auth.AuthController::loginPost");
        Router.Post("/register", "com.mitchdennett.controller.auth.AuthController::registerPost");
    }
}
