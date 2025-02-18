package dev.fralo.bookflix.easyj.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import dev.fralo.bookflix.easyj.auth.AuthToken;
import dev.fralo.bookflix.easyj.auth.User;
import dev.fralo.bookflix.easyj.exceptions.RequestUnauthorizedException;
import dev.fralo.bookflix.easyj.orm.Model;

public class Request {
    private HttpExchange exchange;
    private String method;
    private Headers headers;
    private String rawBody;
    private Gson gson;
    private JsonObject jsonBody;
    private HashMap<String, String> routeParams;
    private User user;

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

    public Request(Request request) throws IOException {
        this.gson = new Gson();
        this.exchange = request.exchange;
        this.method = request.method;
        this.headers = request.headers;
        this.rawBody = request.rawBody;
        this.routeParams = request.routeParams;
        this.jsonBody = request.jsonBody;
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

    // getter for route parameter with type conversion
    public <T> T getRouteParam(String name, Class<T> classOfT, Optional<T> defaultValue) {
        String value = this.routeParams.get(name);
        if (value == null) {
            return defaultValue.orElse(null);
        }

        try {
            return gson.fromJson("\"" + value + "\"", classOfT);
        } catch (Exception e) {
            return defaultValue.orElse(null);
        }
    }

    // overload the getRouteParam method to handle default value
    public <T> T getRouteParam(String name, Class<T> classOfT) {
        return getRouteParam(name, classOfT, Optional.empty());
    }

    public String getRouteParam(String name) {
        return getRouteParam(name, String.class);
    }

    public String getRouteParam(String name, String defaultValue) {
        return getRouteParam(name, String.class, Optional.of(defaultValue));
    }

    public int getRouteParamInt(String name) {
        return getRouteParam(name, Integer.class);
    }

    public int getRouteParamInt(String name, int defaultValue) {
        return getRouteParam(name, Integer.class, Optional.of(defaultValue));
    }

    public Double getRouteParamDouble(String name) {
        return getRouteParam(name, Double.class);
    }

    public double getRouteParamDouble(String name, double defaultValue) {
        return getRouteParam(name, Double.class, Optional.of(defaultValue));
    }

    public Boolean getRouteParamBoolean(String name) {
        return getRouteParam(name, Boolean.class);
    }

    public boolean getRouteParamBoolean(String name, boolean defaultValue) {
        return getRouteParam(name, Boolean.class, Optional.of(defaultValue));
    }

    public Long getRouteParamLong(String name) {
        return getRouteParam(name, Long.class);
    }

    public long getRouteParamLong(String name, long defaultValue) {
        return getRouteParam(name, Long.class, Optional.of(defaultValue));
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

    public boolean isAuthorized() throws RequestUnauthorizedException, SQLException {
        if(this.user != null) {
            return true;
        }

        String bearerToken = this.getHeader("Authorization");
        if (bearerToken == null || bearerToken.length() < "Bearer ".length()) {
            throw new RequestUnauthorizedException("Missing bearer token");
        }

        String tokenValue = bearerToken.substring("Bearer ".length());
        AuthToken authToken = Model.queryBuilder(AuthToken.class).where("value", tokenValue).get();
        if (authToken == null) {
            throw new RequestUnauthorizedException("Unauthorized");
        }
        
        this.user = Model.queryBuilder(User.class).where("id", authToken.getUserId()).get();
        if (this.user == null) {
            throw new RequestUnauthorizedException("Unauthorized");
        }

        return true;
    }

    public User getUser() throws RequestUnauthorizedException, SQLException {
        if(this.user == null) {
            this.isAuthorized();
        }
        return this.user;
    }
}