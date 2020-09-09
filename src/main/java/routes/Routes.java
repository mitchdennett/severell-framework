package {{.Package}}.routes;

import com.severell.core.http.Router;

public class Routes {

    public static void init() throws NoSuchMethodException, ClassNotFoundException {
        Router.Get("/", "{{.Package}}.controller.MainController::index");
        Router.Get("/register", "{{.Package}}.controller.auth.AuthController::register");
        Router.Get("/login", "{{.Package}}.controller.auth.AuthController::login");
        Router.Post("/login", "{{.Package}}.controller.auth.AuthController::loginPost");
        Router.Post("/register", "{{.Package}}.controller.auth.AuthController::registerPost");
    }
}
