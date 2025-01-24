package dev.fralo.bookflix;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dev.fralo.bookflix.core.Request;
import dev.fralo.bookflix.core.Response;
import dev.fralo.bookflix.easyj.db.Database;
import dev.fralo.bookflix.easyj.db.MigrationManager;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.models.User;

public class App {
    public static void main(String[] args) throws IOException, SQLException, Exception {

        startup();
        


        // User u = new User("cannavacciuolo", "gugugaga");
        // u.save();

        User canna = Model.get(User.class, 1);
        System.out.println(canna);
        System.out.println(canna.getId());

        canna.delete();

        
        // Create an HttpServer instance
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Create a context for a specific path and set the handler
        server.createContext("/", new MyHandler());

        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running on port 8000");
    }

    static void startup() throws IOException, SQLException, Exception {
        startDb();
        Model.setDatabase(Database.getConnection());
    }

    static void startDb() throws SQLException, Exception{
        MigrationManager mm = new MigrationManager();
        mm.runMigrations();
    }

    static Connection connect() {
        String url = "jdbc:postgresql://host.docker.internal:15432/";
        String username = "postgres";
        String password = "password";

        Connection c = null;
        try {
            c= DriverManager.getConnection(url, username, password);

            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                System.out.print("Column 1 returned ");
                System.out.println(rs.getString(1));
                System.out.print("Column 2 returned ");
                System.out.println(rs.getString(2));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Impossibile connettersi al database");
            System.err.println(e.getMessage());
        }

        return c;
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
