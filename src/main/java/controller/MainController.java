package {{.Package}}.controller;

import com.mitchdennett.framework.http.Request;
import com.mitchdennett.framework.http.Response;

import java.io.IOException;
import java.util.HashMap;

public class MainController {

    public void index(Request request, Response resp) throws IOException {
        resp.render("index.mustache", new HashMap<String, Object>());
    }

}
