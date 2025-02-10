package dev.fralo.bookflix.easyj.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

public class Response {
    private static final Gson GSON = new Gson();
    private final HttpExchange exchange;
    private boolean headersSent = false;
    private int statusCode = 200;

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public Response status(int statusCode) {
        if (headersSent) {
            throw new IllegalStateException("Headers already sent");
        }
        this.statusCode = statusCode;
        return this;
    }

    public Response header(String name, String value) {
        if (headersSent) {
            throw new IllegalStateException("Headers already sent");
        }
        exchange.getResponseHeaders().add(name, value);
        return this;
    }

    public void send(String content) throws IOException {
        sendInternal(content.getBytes(StandardCharsets.UTF_8), "text/plain");
    }

    public void json(Object data) throws IOException {
        String json = GSON.toJson(data);
        header("Content-Type", "application/json");
        sendInternal(json.getBytes(StandardCharsets.UTF_8), "application/json");
    }

    public void send(int status, String content) throws IOException {
        status(status).send(content);
    }

    public void sendUnauthorized() throws IOException  {
        status(403).send("Unauthorized");
    }

    private void sendInternal(byte[] contentBytes, String contentType) throws IOException {
        if (!headersSent) {
            // Set default content type if not already set
            if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
                exchange.getResponseHeaders().set("Content-Type", contentType);
            }
            // Send response headers
            exchange.sendResponseHeaders(statusCode, contentBytes.length);
            headersSent = true;
        }

        // Write the response body
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(contentBytes);
        }
    }
}