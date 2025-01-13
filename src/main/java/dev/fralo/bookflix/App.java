package dev.fralo.bookflix;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
            // handle the request
            String response = "Hello, this is a simple HTTP server response!";

            System.out.println("Metodo:");
            System.out.println(exchange.getRequestMethod());
            System.out.println("URI:");
            System.out.println(exchange.getRequestURI());
            System.out.println("BODY:");
            System.out.println(this.readStream(exchange.getRequestBody()));
            System.out.println("HEADERS:");
            Headers headers = exchange.getRequestHeaders();

            System.out.print("Authorization: `");
            System.out.print(headers.get("Authorization"));
            System.out.println("`");

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
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
}
