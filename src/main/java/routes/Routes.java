package{{.Package}};

import com.mitchdennett.framework.http.Router;

public class Routes {

    public static void init() throws NoSuchMethodException, ClassNotFoundException {
        Router.Get("/", "com.mitchdennett.controller.MainController::index");
        Router.Get("/blog/:id", "com.mitchdennett.controller.MainController::blog");
        Router.Get("/blog/:id/video", "com.mitchdennett.controller.MainController::index");
        Router.Get("/blogging", "com.mitchdennett.controller.MainController::blog");
        Router.Get("/about", "com.mitchdennett.controller.MainController::blog");
        Router.Get("/beach", "com.mitchdennett.controller.MainController::blog");
    }
}
