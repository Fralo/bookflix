package dev.fralo.bookflix.core;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

public class Response {

    private HttpExchange exchange;
    private Gson gson;

    public Response(HttpExchange exchange) throws IOException {
        this.gson = new Gson();

        this.exchange = exchange;
    }
    
    public void send(int status, String content) throws IOException {
        this.exchange.sendResponseHeaders(status, content.length());
        OutputStream os = exchange.getResponseBody();
        os.write(content.getBytes());
        os.close();
    }
}
