package dev.fralo.bookflix.easyj.routing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import dev.fralo.bookflix.easyj.annotations.router.Get;
import dev.fralo.bookflix.easyj.annotations.router.PathParam;
import dev.fralo.bookflix.easyj.annotations.router.Post;

public class Router {
    private static Router instance;
    private final List<Route> routes = new ArrayList<>();
    private final Map<String, Pattern> pathPatternCache = new HashMap<>();

    private Router() {}

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    /**
     * Registers all routes from a controller class
     */
    public void registerController(Class<? extends Controller> controllerClass) {
        for (Method method : controllerClass.getDeclaredMethods()) {
            registerAnnotatedMethod(controllerClass, method);
        }
    }

    /**
     * Creates routes from annotated controller methods
     */
    private void registerAnnotatedMethod(Class<?> controllerClass, Method method) {
        Arrays.stream(method.getAnnotations())
            .filter(annotation -> annotation instanceof Get || annotation instanceof Post)
            .forEach(annotation -> {
                RouteInfo routeInfo = extractRouteInfo(annotation, method);
                if (routeInfo == null) return;

                routes.add(new Route(
                    routeInfo.httpMethod(),
                    routeInfo.path(),
                    createRouteHandler(controllerClass, method)
                ));
            });
    }

    /**
     * Creates a handler for a controller method
     */
    private RouteHandler createRouteHandler(Class<?> controllerClass, Method method) {
        return (req, res) -> {
            try {
                Constructor<?> constructor = controllerClass.getDeclaredConstructor(Request.class, Response.class);
                constructor.setAccessible(true);
                Object controllerInstance = constructor.newInstance(req, res);
                Object[] args = resolveParameters(method, req, req.path());
                System.out.println("Ho ricevuto " + args.length);
                System.out.println(args);
                return method.invoke(controllerInstance, args);
            } catch (Exception e) {
                throw new RuntimeException("Failed to handle route", e);
            }
        };
    }

    /**
     * Matches a request path against a route pattern
     */
    public Optional<RouteMatch> matchRoute(String method, String path) {
        return routes.stream()
            .filter(route -> route.method().equalsIgnoreCase(method))
            .map(route -> {
                Pattern pattern = pathPatternCache.computeIfAbsent(
                    route.path(),
                    this::createPathPattern
                );
                Matcher matcher = pattern.matcher(path);
                if (matcher.matches()) {
                    Map<String, String> params = extractPathParams(route.path(), path);
                    return new RouteMatch(route, params);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst();
    }

    /**
     * Creates a regex pattern for matching route paths
     */
    private Pattern createPathPattern(String routePath) {
        String[] segments = routePath.split("/");
        StringBuilder patternBuilder = new StringBuilder("^");
        
        for (String segment : segments) {
            if (segment.isEmpty()) continue;
            patternBuilder.append("/");
            if (segment.startsWith("{") && segment.endsWith("}")) {
                patternBuilder.append("([^/]+)");
            } else {
                patternBuilder.append(Pattern.quote(segment));
            }
        }
        
        patternBuilder.append("$");
        return Pattern.compile(patternBuilder.toString());
    }

    /**
     * Extracts path parameters from a request path
     */
    private Map<String, String> extractPathParams(String routePath, String requestPath) {
        Map<String, String> params = new HashMap<>();
        String[] routeParts = routePath.split("/");
        String[] requestParts = requestPath.split("/");

        for (int i = 0; i < routeParts.length; i++) {
            if (routeParts[i].startsWith("{") && routeParts[i].endsWith("}")) {
                String paramName = routeParts[i].substring(1, routeParts[i].length() - 1);
                params.put(paramName, requestParts[i]);
            }
        }

        return params;
    }

    /**
     * Creates an HttpHandler for use with HttpServer
     */
    public HttpHandler createHandler() {
        return (HttpExchange exchange) -> {
            Request request = new Request(exchange);
            Response response = new Response(exchange);

            try {
                System.out.println("ho ricevuto");
                System.out.println(request.path());
                Optional<RouteMatch> matchResult = matchRoute(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI().getPath()
                );

                if (matchResult.isPresent()) {
                    RouteMatch match = matchResult.get();
                    request.setPathParams(match.params());
                    match.route().handler().handle(request, response);
                } else {
                    response.status(404).send("Not Found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.status(500).send("Internal Server Error");
            }
        };
    }

    /**
     * Helper records and interfaces
     */
    private record RouteInfo(String httpMethod, String path) {}
    
    private record RouteMatch(Route route, Map<String, String> params) {}

    @FunctionalInterface
    public interface RouteHandler {
        Object handle(Request request, Response response) throws Exception;
    }

    private RouteInfo extractRouteInfo(Annotation annotation, Method method) {
        if (annotation instanceof Get) {
            return new RouteInfo("GET", method.getAnnotation(Get.class).value());
        }
        if (annotation instanceof Post) {
            return new RouteInfo("POST", method.getAnnotation(Post.class).value());
        }
        return null;
    }

    private Object[] resolveParameters(Method method, Request request, String pathPattern) {
        return Arrays.stream(method.getParameters())
            .<Object>map(param -> {
                if (param.isAnnotationPresent(PathParam.class)) {
                    String paramName = param.getAnnotation(PathParam.class).value();
                    try {
                        String paramValue = request.getPathParameter(pathPattern, paramName);
                        // Convert the parameter to the correct type
                        if (param.getType() == String.class) {
                            return paramValue;
                        } else if (param.getType() == Integer.class || param.getType() == int.class) {
                            return Integer.parseInt(paramValue);
                        } else if (param.getType() == Long.class || param.getType() == long.class) {
                            return Long.parseLong(paramValue);
                        } else {
                            throw new IllegalArgumentException("Unsupported parameter type: " + param.getType());
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to resolve parameter '" + paramName + "': " + e.getMessage());
                        return null;
                    }
                }
                return null;
            })
            .filter(Objects::nonNull)
            .toArray();
    }
}