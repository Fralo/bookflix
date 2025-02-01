package dev.fralo.bookflix.core;

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