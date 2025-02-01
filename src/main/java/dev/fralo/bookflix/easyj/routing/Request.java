package dev.fralo.bookflix.easyj.routing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

public class Request {
    private static final Gson GSON = new Gson();
    private final HttpExchange exchange;
    private final String method;
    private final String body;
    private Map<String, String> pathParams = Collections.emptyMap();

    public Request(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        this.method = exchange.getRequestMethod();
        this.body = readBody(exchange.getRequestBody());
    }

    public String method() {
        return method;
    }

    public String path() {
        return exchange.getRequestURI().getPath();
    }

    public String query() {
        return exchange.getRequestURI().getQuery();
    }

    public String header(String name) {
        return exchange.getRequestHeaders().getFirst(name);
    }

    public String param(String name) {
        return pathParams.getOrDefault(name, "");
    }

    public <T> T body(Class<T> type) {
        return GSON.fromJson(body, type);
    }

    void setPathParams(Map<String, String> params) {
        this.pathParams = new HashMap<>(params);
    }

    /**
     * Gets a path parameter value from the current request path.
     * 
     * @param routePattern The route pattern with parameters (e.g., "/users/{id}/posts/{postId}")
     * @param paramName The name of the parameter to extract (e.g., "id")
     * @return The value of the path parameter
     * @throws Exception if the parameter cannot be found or the path doesn't match the pattern
     */
    public String getPathParameter(String routePattern, String paramName) throws Exception {
        // Normalize paths by removing trailing slashes and ensuring leading slash
        String normalizedPattern = normalizePath(routePattern);
        String normalizedPath = normalizePath(path());

        // Split both paths into segments
        String[] patternSegments = normalizedPattern.split("/");
        String[] pathSegments = normalizedPath.split("/");

        // Verify path lengths match
        if (patternSegments.length != pathSegments.length) {
            throw new Exception(String.format(
                "Path length mismatch. Pattern has %d segments, but path has %d segments.",
                patternSegments.length, pathSegments.length));
        }

        // Look for parameter in pattern and get corresponding value from path
        for (int i = 0; i < patternSegments.length; i++) {
            String patternSegment = patternSegments[i];
            
            // Check if current segment is a parameter
            if (isParameterSegment(patternSegment)) {
                // Extract parameter name without braces
                String currentParamName = extractParamName(patternSegment);
                
                // If this is the parameter we're looking for, return the actual path value
                if (currentParamName.equals(paramName)) {
                    return pathSegments[i];
                }
            }
            // For non-parameter segments, verify they match exactly
            else if (!patternSegment.equals(pathSegments[i])) {
                throw new Exception(String.format(
                    "Path segment mismatch at position %d. Expected '%s' but got '%s'",
                    i, patternSegment, pathSegments[i]));
            }
        }
        
        throw new Exception(String.format(
            "Parameter '%s' not found in route pattern '%s'",
            paramName, routePattern));
    }

    /**
     * Normalizes a path by ensuring it has a leading slash and no trailing slash
     */
    private String normalizePath(String path) {
        String normalized = path.startsWith("/") ? path : "/" + path;
        normalized = normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
        return normalized;
    }

    /**
     * Checks if a path segment represents a parameter (enclosed in curly braces)
     */
    private boolean isParameterSegment(String segment) {
        return segment.startsWith("{") && segment.endsWith("}") && segment.length() > 2;
    }

    /**
     * Extracts the parameter name from a parameter segment by removing the curly braces
     */
    private String extractParamName(String segment) {
        return segment.substring(1, segment.length() - 1);
    }

    private String readBody(InputStream stream) throws IOException {
        try(InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[4096];
            int read;
            while((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        }
    }
}