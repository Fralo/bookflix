package dev.fralo.bookflix.easyj.bootstrappers;

import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;
import dev.fralo.bookflix.easyj.routing.Router;

public class RouterBootstrapper extends Bootstrapper {

    private final Router router;

    public RouterBootstrapper() {
        this.router = Router.getInstance();
    }

    @Override
    public void bootstrap() throws Exception {
        this.registerRoutes();
    }

    private void registerRoutes() {
        router.register("GET", "/users", (Request req, Response res) -> {
            String result = "Hai inviato una " + req.getMethod() + " a " + req.getUri();

            res.send(200, result);
        });
    }
}