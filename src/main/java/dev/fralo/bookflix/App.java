package dev.fralo.bookflix;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.net.httpserver.HttpServer;

import dev.fralo.bookflix.controllers.UserController;
import dev.fralo.bookflix.easyj.db.Database;
import dev.fralo.bookflix.easyj.db.MigrationManager;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.easyj.routing.Router;

public class App {
    public static void main(String[] args) throws IOException, SQLException, Exception {
        startup();

        // Create an HttpServer instance
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        Router router = Router.getInstance();
        

        server.createContext("/", router.createHandler());
        server.start();

        System.out.println("Server is running on port 8000");
    }

    static void startup() throws IOException, SQLException, Exception {
        startDb();
        Model.setDatabase(Database.getConnection());
        startRouter();
    }

    static void startRouter() {
        Router router = Router.getInstance();
        router.registerController(UserController.class);
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
}
