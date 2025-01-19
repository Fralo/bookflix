package dev.fralo.bookflix.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Migration {
    private String name;
    private String query;
    
    
    public Migration(File migrationFile) throws Exception {
        System.out.println(migrationFile.getName());
        this.name = migrationFile.getName().split(".sql")[0];
        this.query = this.readQueryFromFile(migrationFile);
    }

    public String getName() { return this.name;}

    private String readQueryFromFile(File file) throws Exception {
        try {
            // Create a Scanner object to read the file
            Scanner scanner = new Scanner(file);

            String res = "";
            // Read the file content
            while (scanner.hasNextLine()) {
                res += scanner.nextLine() + " ";
            }

            // Close the Scanner object
            scanner.close();
            return res.trim();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            throw new Exception("Migrazione non corretta");
        }
    }

    public void run() throws SQLException {
        this.executedMigration();
        this.addToExecutedMigrations();
    }

    private void executedMigration() throws SQLException {
        Statement st = Database.getConnection().createStatement();
        System.out.println("Query: " + this.query);
        st.executeUpdate(this.query);
        st.close(); 
    }

    private void addToExecutedMigrations() throws SQLException {
        Statement st = Database.getConnection().createStatement();
        String updateMigrationsQuery = "INSERT INTO migrations(name) VALUES('" + this.name + "')";
        System.out.println("Query: " + updateMigrationsQuery);
        st.executeUpdate(updateMigrationsQuery);
        st.close(); 
    }
    
}
