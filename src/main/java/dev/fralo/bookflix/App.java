package dev.fralo.bookflix;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dev.fralo.bookflix.core.Request;
import dev.fralo.bookflix.core.Response;
import dev.fralo.bookflix.easyj.db.Database;
import dev.fralo.bookflix.easyj.db.MigrationManager;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.easyj.routing.Router;

public class App {
    public static void main(String[] args) throws IOException, SQLException, Exception {
        int port = 8000;
        bootstrap();
                
        // Create an HttpServer instance
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create a context for a specific path and set the handler
        server.createContext("/", new RequestHandler());

        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running on port " + String.valueOf(port) );
    }

    static void bootstrap() throws IOException, SQLException, Exception {
        bootstrapDatabase();
        Model.setDatabase(Database.getConnection());
    }

    static void bootstrapDatabase() throws SQLException, Exception{
        MigrationManager mm = new MigrationManager();
        mm.runMigrations();
    }

    // define a custom HttpHandler
    static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // handle the request
                Request request = new Request(exchange);
                Response response = new Response(exchange);
                Router router = Router.getInstance();

                router.register("GET", "/users", (Request req, Response res) -> {
                    String result = "Hai inviato una mannaggia a te " + req.getMethod() + " a " + req.getUri();

                    response.send(200, result);
                });

                router.handle(request, response);

                
                
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
