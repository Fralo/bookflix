package dev.fralo.bookflix;


import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dev.fralo.bookflix.core.Request;
import dev.fralo.bookflix.core.Response;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        // Create an HttpServer instance
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Create a context for a specific path and set the handler
        server.createContext("/", new MyHandler());

        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running on port 8000");
    }

    // define a custom HttpHandler
    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // handle the request
                Request request = new Request(exchange);

                String result = "Hai inviato una " + request.getMethod() + " a " + request.getUri();

                Response response = new Response(exchange);
                response.send(200, result);
                
            }catch(Exception e) {
                System.out.println(e.getClass());
                String errorMessage = e.getMessage();
                System.err.println(errorMessage);

                Response errorResponse = new Response(exchange);
                errorResponse.send(400, errorMessage);
            }
        }
    }
}
