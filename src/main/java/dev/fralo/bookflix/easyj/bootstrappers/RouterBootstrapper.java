package dev.fralo.bookflix.easyj.bootstrappers;

import java.util.ArrayList;
import java.util.List;

import dev.fralo.bookflix.controllers.UserController;
import dev.fralo.bookflix.easyj.routing.Controller;

public class RouterBootstrapper extends Bootstrapper {
    public static List<Controller> controllers = new ArrayList<Controller>() {
        {
            add(new UserController());
        };
    };

    @Override
    public void bootstrap() throws Exception {
        this.registerRoutes();
    }

    private void registerRoutes() {
        for (Controller controller : controllers) {
            controller.register();
        }
    }
}