package dev.fralo.bookflix.easyj.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:postgresql://0.0.0.0:15432/";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password";

    private static Connection connection;

    
    public static Connection getConnection() throws SQLException {
        if(connection == null) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }

        return connection;
    }
}
