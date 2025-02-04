package dev.fralo.bookflix.easyj.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class Request {

    private HttpExchange exchange;
    private String method;
    private Headers headers;
    private String rawBody;
    private Gson gson;

    public Request(HttpExchange exchange) throws IOException {
        this.gson = new Gson();

        this.exchange = exchange;
        this.method = exchange.getRequestMethod();
        this.headers = exchange.getRequestHeaders();
        this.rawBody = this.readStream(exchange.getRequestBody());
    }
    
    public String getMethod() {
        return this.method;
    }

    public String getHeader(String headerName) {
        if(!this.headers.containsKey(headerName)) {
            return "";
        }

        return this.headers.getFirst(headerName);
    }

    public String getUri() {
        return this.exchange.getRequestURI().toString();
    }

    public Object getBody() {
        try {
            return "NOT IMPLEMENTED";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
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
