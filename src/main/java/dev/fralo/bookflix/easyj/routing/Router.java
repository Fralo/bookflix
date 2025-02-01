package dev.fralo.bookflix.easyj.routing;

import java.util.HashMap;

import dev.fralo.bookflix.core.Request;
import dev.fralo.bookflix.core.Response;

public class Router {
    private static Router router;
    private final HashMap<String, RouteHandler> routes = new HashMap<>();
    
    private Router() {

    }

    public static Router getInstance() {
        if(router == null) {
           router = new Router();
        }

        return router;
    }

    public void register(String method, String path, RouteHandler handler) {
        routes.put(method + "-" + path, handler);
    }

    public void handle(Request request, Response response) throws Exception {
        RouteHandler handler = getRoute(request);

        if(handler == null) {
            throw new Exception("Impossibile trovare la route: " + request.getUri());
        }

        handler.handle(request, response);
    }

    private RouteHandler getRoute(Request request) {
        String methodPath = request.getMethod() + "-" + request.getUri();
        return routes.get(methodPath);
    }

}


