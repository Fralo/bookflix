package dev.fralo.bookflix.controllers;

import dev.fralo.bookflix.easyj.auth.AuthToken;
import dev.fralo.bookflix.easyj.auth.User;
import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.easyj.routing.Controller;
import dev.fralo.bookflix.easyj.utils.PasswordHasher;
import dev.fralo.bookflix.requests.LoginRequest;
import dev.fralo.bookflix.requests.RegistrationRequest;

public class UserController extends Controller {
    public UserController() {
        super();
        this.basePath = "/users";
    }

    @Override
    public void register() {
        this.get("/whoami", (Request req, Response res) -> {
            res.json(req.getUser());
        });

        this.post("/register", (Request req, Response res) -> {
            RegistrationRequest registrationReq = req.getBody(RegistrationRequest.class);

            String email = registrationReq.email;
            String password = registrationReq.password;

            if (email == null | password == null) {
                res.send(400, "Invalid mail or password");
                return;
            }

            User user = Model.queryBuilder(User.class).where("email", email).get();
            if (user != null) {
                res.send(400, "The email was already used");
                return;
            }

            PasswordHasher passwordHasher = new PasswordHasher();
            User u = new User(
                    registrationReq.email,
                    passwordHasher.hashPassword(registrationReq.password));
            u.save();

            res.json(u, 201);
        });

        this.patch("/{id}", (Request req, Response res) -> {
            User user = req.getUser();

            if(user.getId() != req.getRouteParamInt("id")) {
                res.send(401, "Unauthorized");
                return;
            }

            res.json(user);
        });

        this.delete("/{id}", (Request req, Response res) -> {
            User user = req.getUser();

            if(user.getId() !=  req.getRouteParamInt("id")) {
                res.send(401, "Unauthorized");
                return;
            }

            Model.queryBuilder(User.class).where("id", req.getRouteParamInt("id")).delete();
            res.send(200, "");
        });

        this.post("/login", (Request req, Response res) -> {
            LoginRequest loginRequest = req.getBody(LoginRequest.class);

            String email = loginRequest.email;
            String password = loginRequest.password;

            if (email == null | password == null) {
                res.send(400, "Invalid mail or password");
                return;
            }

            User user = Model.queryBuilder(User.class).where("email", email).get();
            if (user == null) {
                res.send(404, "Not found");
                return;
            }

            PasswordHasher passwordHasher = new PasswordHasher();

            if (!passwordHasher.verifyPassword(password, user.getPassword())) {
                res.send(403, "Wrong email or passoword");
            }

            // we create the auth token
            AuthToken authToken = new AuthToken(user);
            authToken.save();

            res.json(authToken);
        });
    }

}
