package dev.fralo.bookflix.controllers;

import dev.fralo.bookflix.controllers.requests.RegistrationRequest;
import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;
import dev.fralo.bookflix.easyj.routing.Controller;
import dev.fralo.bookflix.models.User;

public class UserController extends Controller {
    public UserController() {
        super();
        this.basePath = "/users";
    }

    @Override
    public void register() {
        this.get("/{id}", (Request req, Response res) -> {
            User u = User.get(
                User.class,
                req.getRouteParamInt("id")
            );
            res.json(u);
        });

        this.post("/register", (Request req, Response res) -> {
            RegistrationRequest registrationReq = req.getBody(RegistrationRequest.class);

            User u = new User(registrationReq.email, registrationReq.password);
            u.save();

            res.json(u);
        });
    }

}
