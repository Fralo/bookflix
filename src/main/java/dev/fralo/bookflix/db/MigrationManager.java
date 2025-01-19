package dev.fralo.bookflix.db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class MigrationManager {

    private Connection db;
    private MigrationManager manager;
    private String migrationsPath = "./migrations";
    private String migrationsTableName = "migrations";

    public MigrationManager() throws SQLException {
        this.db = Database.getConnection();

        if(!this.migrationTableExists()) {
            this.createMigrationsTable();
        }
    };

    private boolean migrationTableExists() throws SQLException {
        Statement st = this.db.createStatement();
        String query = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = '" + this.migrationsTableName + "')";
        System.out.println(query);
        ResultSet rs = st.executeQuery(query);
        boolean result = false;
        while (rs.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(rs.getBoolean(1));
            result = rs.getBoolean(1);
            break;
        }
        rs.close();
        st.close();
        return result;
    }

    private void createMigrationsTable() throws SQLException {
        Statement st = this.db.createStatement();
        String query = "CREATE TABLE IF NOT EXISTS " + this.migrationsTableName + " (" +
            "id SERIAL PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL)";
        System.out.println(query);
        st.executeUpdate(query);
        st.close(); 
    };

    public void runMigrations() throws SQLException, Exception {
        ArrayList<String> executedMigrations = this.getExecutedMigrations();
        File[] migrationFiles = this.readMigrationFiles();

        for(File migrationFile : migrationFiles) {
            Migration migration = new Migration(migrationFile);

            System.out.println(executedMigrations);
            System.out.println(migration.getName());

            if (!executedMigrations.contains(migration.getName())) {
                migration.run();
            }
        }

    }

    private File[] readMigrationFiles() {
        File migrationFolder = new File(this.migrationsPath);

        // Check if the folder exists
        if (migrationFolder.exists() && migrationFolder.isDirectory()) {
            // Get the list of files in the folder
            File[] files = migrationFolder.listFiles();
            Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            return files;
        } else {
            System.err.println("Folder not found or not a directory: " + this.migrationsPath);
        }
        return new File[0];
    }

    private ArrayList<String> getExecutedMigrations() throws SQLException {
        Statement st = this.db.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + this.migrationsTableName);
        ArrayList<String> result = new ArrayList<>();

        System.out.println("rs.getString(2)");

        while (rs.next()) {
            System.out.println("aaaa");
            System.out.println(rs.getString(2));

            System.out.println(rs.getString(2));
            result.add(rs.getString(2));   
        }
        rs.close();
        st.close();
        return result;
    }
}
