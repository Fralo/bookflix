package dev.fralo.bookflix.easyj.routing;

/**
 * Represents a route in the routing system.
 * Each route contains an HTTP method, path pattern, and handler.
 */
public class Route {
    private final String method;
    private final String path;
    private final Router.RouteHandler handler;

    /**
     * Creates a new route with the specified HTTP method, path pattern, and handler.
     * 
     * @param method The HTTP method (GET, POST, etc.)
     * @param path The path pattern (e.g., "/users/{id}")
     * @param handler The handler to process requests matching this route
     */
    public Route(String method, String path, Router.RouteHandler handler) {
        this.method = method.toUpperCase();
        // Ensure path starts with forward slash
        this.path = path.startsWith("/") ? path : "/" + path;
        this.handler = handler;
    }

    /**
     * Gets the HTTP method for this route.
     * 
     * @return The HTTP method (GET, POST, etc.)
     */
    public String method() {
        return method;
    }

    /**
     * Gets the path pattern for this route.
     * 
     * @return The path pattern (e.g., "/users/{id}")
     */
    public String path() {
        return path;
    }

    /**
     * Gets the handler for this route.
     * 
     * @return The route handler
     */
    public Router.RouteHandler handler() {
        return handler;
    }

    /**
     * Determines if this route matches the given HTTP method and path.
     * 
     * @param method The HTTP method to match
     * @param path The path to match
     * @return true if the method and path match this route
     */
    public boolean matches(String method, String path) {
        return this.method.equalsIgnoreCase(method) && matchesPath(path);
    }

    /**
     * Checks if the given path matches this route's path pattern.
     * 
     * @param path The path to check
     * @return true if the path matches the pattern
     */
    private boolean matchesPath(String path) {
        String[] routeParts = this.path.split("/");
        String[] pathParts = path.split("/");

        if (routeParts.length != pathParts.length) {
            return false;
        }

        for (int i = 0; i < routeParts.length; i++) {
            String routePart = routeParts[i];
            String pathPart = pathParts[i];

            // Skip empty parts
            if (routePart.isEmpty()) {
                continue;
            }

            // If it's a path parameter (e.g., {id}), it matches anything
            if (routePart.startsWith("{") && routePart.endsWith("}")) {
                continue;
            }

            // For regular path segments, they must match exactly
            if (!routePart.equals(pathPart)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %s", method, path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Route other = (Route) obj;
        return method.equals(other.method) && path.equals(other.path);
    }

    @Override
    public int hashCode() {
        return 31 * method.hashCode() + path.hashCode();
    }
}