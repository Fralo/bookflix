package dev.fralo.bookflix.easyj.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class Request {
    private HttpExchange exchange;
    private String method;
    private Headers headers;
    private String rawBody;
    private Gson gson;
    private JsonObject jsonBody;
    private HashMap<String, String> routeParams;

    public Request(HttpExchange exchange) throws IOException {
        this.gson = new Gson();
        this.exchange = exchange;
        this.method = exchange.getRequestMethod();
        this.headers = exchange.getRequestHeaders();
        this.rawBody = this.readStream(exchange.getRequestBody());
        this.routeParams = new HashMap<>();

        // if request is content type json, we parse the body
        String contentType = this.getHeader("Content-Type");
        if (contentType.toLowerCase().contains("application/json")) {
            try {
                this.jsonBody = JsonParser.parseString(this.rawBody).getAsJsonObject();
            } catch (Exception e) {
                this.jsonBody = new JsonObject();
            }
        }
    }

    public void setRouteParams(HashMap<String, String> params) {
        this.routeParams = params;
    }

    public void setRouteParam(String name, String value) {
        this.routeParams.put(name, value);
    }

    public String getMethod() {
        return this.method;
    }

    public String getHeader(String headerName) {
        if (!this.headers.containsKey(headerName)) {
            return "";
        }
        return this.headers.getFirst(headerName);
    }

    public String getUri() {
        return this.exchange.getRequestURI().toString();
    }

    public String getRouteParam(String name) {
        return this.routeParams.get(name);
    }

    public String getRawBody() {
        return this.rawBody;
    }

    public <T> T getBody(Class<T> classOfT) {
        try {
            return gson.fromJson(this.rawBody, classOfT);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T getBodyField(String fieldName, Class<T> classOfT, Optional<T> defaultValue) {
        if (this.jsonBody == null) {
            return defaultValue.orElse(null);
        }

        try {
            JsonElement element = this.jsonBody.get(fieldName);
            if (element == null || element.isJsonNull()) {
                return defaultValue.orElse(null);
            }
            return gson.fromJson(element, classOfT);
        } catch (Exception e) {
            return defaultValue.orElse(null);
        }
    }

    public <T> T getBodyField(String fieldName, Class<T> classOfT) {
        return getBodyField(fieldName, classOfT, Optional.empty());
    }

    // i'm overloading the fields becuse i think it leads to a cleaner API

    public String getBodyString(String fieldName) {
        return getBodyField(fieldName, String.class);
    }

    public String getBodyString(String fieldName, String defaultValue) {
        return getBodyField(fieldName, String.class, Optional.of(defaultValue));
    }

    public Integer getBodyInt(String fieldName) {
        return getBodyField(fieldName, Integer.class);
    }

    public int getBodyInt(String fieldName, int defaultValue) {
        return getBodyField(fieldName, Integer.class, Optional.of(defaultValue));
    }

    public Double getBodyDouble(String fieldName) {
        return getBodyField(fieldName, Double.class);
    }

    public double getBodyDouble(String fieldName, double defaultValue) {
        return getBodyField(fieldName, Double.class, Optional.of(defaultValue));
    }

    public Boolean getBodyBoolean(String fieldName) {
        return getBodyField(fieldName, Boolean.class);
    }

    public boolean getBodyBoolean(String fieldName, boolean defaultValue) {
        return getBodyField(fieldName, Boolean.class, Optional.of(defaultValue));
    }

    private String readStream(InputStream stream) throws IOException {
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0;) {
            out.append(buffer, 0, numRead);
        }
        return out.toString();
    }
}