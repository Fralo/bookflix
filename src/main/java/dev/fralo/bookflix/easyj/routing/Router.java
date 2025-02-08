package dev.fralo.bookflix.easyj.routing;

import java.util.HashMap;
import java.util.Set;

import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;

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

        // doesn't have directly the route
        if (!routes.containsKey(methodPathKey)) {
            methodPathKey = this.getMatchingPath(request, method, uri);

            if (methodPathKey == null) {
                return null;
            }
        }
        return this.routes.get(methodPathKey);
    }

    private String getMatchingPath(Request request, String method, String uri) {
        Set<String> keys = routes.keySet();

        for (String key : keys) {
            String subKey = key.replace(method.concat("-"), "");

            String[] pathParts = subKey.split("/");
            String[] uriParts = uri.split("/");

            // not the same length
            if (pathParts.length != uriParts.length) {
                continue;
            }
            Boolean notMatching = false;
            HashMap<String, String> collecterdParameters = new HashMap<>();
            for (int i = 0; (i < pathParts.length); i++) {
                if (pathParts[i].equals(uriParts[i]) && !pathParts[i].startsWith("{")) {
                    continue;
                }

                if (!pathParts[i].startsWith("{")) {
                    notMatching = true;
                    break;
                }

                collecterdParameters.put(
                        pathParts[i].substring(1, pathParts[i].length() - 1),
                        uriParts[i]);
            }

            if (notMatching) {
                continue;
            }

            // otherwise everything has matched! set the parameters on the request and
            // return the path
            request.setRouteParams(collecterdParameters);
            return key;
        }

        // nothing matched
        return null;
    }

    private String buildKey(String method, String path) {
        return method.concat("-").concat(path);
    }

}
