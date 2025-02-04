package dev.fralo.bookflix;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.fralo.bookflix.easyj.bootstrappers.Bootstrapper;
import dev.fralo.bookflix.easyj.bootstrappers.DatabaseBootstrapper;
import dev.fralo.bookflix.easyj.bootstrappers.HttpServerBootstrapper;
import dev.fralo.bookflix.easyj.bootstrappers.OrmBootstrapper;
import dev.fralo.bookflix.easyj.bootstrappers.RouterBootstrapper;

public class App {

    public static List<Bootstrapper> bootstrappers = new ArrayList<Bootstrapper>() {
        {
            add(new DatabaseBootstrapper());
            add(new OrmBootstrapper());
            add(new RouterBootstrapper());
            add(new HttpServerBootstrapper());
        };
    };


    public static void main(String[] args) throws IOException, SQLException, Exception {
        bootstrap();
    }

    static void bootstrap() throws IOException, SQLException, Exception {
        for (Bootstrapper bootstrapper : bootstrappers) {
            bootstrapper.bootstrap();
        }
    }
}
