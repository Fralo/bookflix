package dev.fralo.bookflix.controllers;

import dev.fralo.bookflix.controllers.requests.RegistrationRequest;
import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.easyj.routing.Controller;
import dev.fralo.bookflix.easyj.utils.PasswordHasher;
import dev.fralo.bookflix.models.User;

public class UserController extends Controller {
    public UserController() {
        super();
        this.basePath = "/users";
    }

    @Override
    public void register() {
        this.get("/{id}", (Request req, Response res) -> {
            User user = Model.queryBuilder(User.class).where("id", req.getRouteParamInt("id")).get();

            if(user == null) {
                res.send(404, "Not found");
            }

            res.json(user);
        });

        this.post("/register", (Request req, Response res) -> {
            RegistrationRequest registrationReq = req.getBody(RegistrationRequest.class);

            String email = registrationReq.email;
            String password = registrationReq.password;

            if(email == null | password == null) {
                res.send(400, "Invalid mail or password");
                return;
            }

            User user = Model.queryBuilder(User.class).where("email", email).get();
            if(user != null) {
                res.send(400, "The email was already used");
                return;
            }

            PasswordHasher passwordHasher = new PasswordHasher();
            User u = new User(
                registrationReq.email,
                passwordHasher.hashPassword(registrationReq.password)
            );
            u.save();

            res.json(u);
        });

        this.patch("/{id}", (Request req, Response res) -> {
            User user = Model.queryBuilder(User.class).where("id", req.getRouteParamInt("id")).get();

            if(user == null) {
                res.send(404, "Not found");
            }

            res.json(user);
        });

        this.delete("/{id}", (Request req, Response res) -> {
            Model.queryBuilder(User.class).where("id", req.getRouteParamInt("id")).delete();
            res.send(200, "");
        });
    }

}
