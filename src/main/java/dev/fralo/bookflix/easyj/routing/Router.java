package dev.fralo.bookflix.easyj.routing;

import java.util.HashMap;

import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;

class PathMatcher {
    public static boolean matches(String routePath, String requestPath) {
        String[] routeParts = routePath.split("/");
        String[] requestParts = requestPath.split("/");

        if(routeParts.length != requestParts.length) {
            return false;
        }

        for (int i = 0; (i < routeParts.length); i++) {
            if (isParameter(routeParts[i])) {
                continue;
            }

            if (!routeParts[i].equals(requestParts[i])) {
                return false;
            }
        }

        return true;
    }

    public static HashMap<String, String> extractParameters(String routePath, String requestPath) {
        HashMap<String, String> params = new HashMap<>();
        String[] routeParts = routePath.split("/");
        String[] requestParts = requestPath.split("/");

        for (int i = 0; i < routeParts.length; i++) {
            if (isParameter(routeParts[i])) {
                String paramName = routeParts[i].substring(1, routeParts[i].length() - 1);
                params.put(paramName, requestParts[i]);
            }
        }

        return params;
    }

    private static boolean isParameter(String value) {
        return value.startsWith("{") && value.endsWith("}");
    }
}


public class Router {
    private static Router router;
    private final HashMap<String, RouteHandler> routes = new HashMap<>();

    private Router() {

    }

    public static Router getInstance() {
        if (router == null) {
            router = new Router();
        }

        return router;
    }

    public void register(String method, String path, RouteHandler handler) {
        routes.put(this.buildKey(method, path), handler);
    }

    public void handle(Request request, Response response) throws Exception {
        RouteHandler handler = getRoute(request);

        if (handler == null) {
            throw new Exception("Impossibile trovare la route: " + request.getUri());
        }

        handler.handle(request, response);
    }

    private RouteHandler getRoute(Request request) {
        String method = request.getMethod();
        String uri = request.getUri();

        String methodPathKey = this.buildKey(method, uri);

        // does have directly the route
        if (routes.containsKey(methodPathKey)) {
            return this.routes.get(methodPathKey);
        }

        //we try to match with parameters
        for (String routeKey : routes.keySet()) {
            if(!routeKey.startsWith(method.concat("-"))) {
                continue;
            }

            String routePath = routeKey.substring(method.length()+1); //removes method and separator (ex. GET-)
            if(PathMatcher.matches(routePath, uri)) {
                request.setRouteParams(PathMatcher.extractParameters(routePath, uri));
                return this.routes.get(routeKey);
            }
        }
        
        return null;
    }

    private String buildKey(String method, String path) {
        return method.concat("-").concat(path);
    }

}
