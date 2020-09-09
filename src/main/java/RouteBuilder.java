package {{.Package}};

import com.mitchdennett.framework.http.MiddlewareExecutor;
import com.mitchdennett.framework.http.RouteExecutor;

import java.util.ArrayList;

public class RouteBuilder {
  public ArrayList<RouteExecutor> build() {return new ArrayList<>();}

  public ArrayList<MiddlewareExecutor> buildDefaultMiddleware() {
      return new ArrayList<>();
  }
}
