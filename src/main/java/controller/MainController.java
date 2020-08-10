package{{.Package}};

import com.mitchdennett.framework.http.Request;
import com.mitchdennett.framework.http.Response;

import java.io.IOException;
import java.util.ArrayList;

public class MainController {

    public static void index(Request request, Response resp) throws IOException {
        resp.view("index.mustache", new ArrayList<Object>());
    }

    public static void blog(Request request, Response resp) throws IOException {
        System.out.println(request.getParam("id"));
        resp.view("blog.mustache", new ArrayList<Object>());
    }
}
