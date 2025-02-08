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
            User u = User.get(User.class, Integer.parseInt(req.getRouteParam("id")));
            res.json(u);
        });

        this.post("/register", (Request req, Response res) -> {
            RegistrationRequest registrationReq = req.getBody(RegistrationRequest.class);

            User u = new User(registrationReq.email, registrationReq.password);
            u.save();

            res.json(u);
        });

        this.get("/{id}/books/{book_id}", (Request req, Response res) -> {
            String userId = req.getRouteParam("id");
            String bookId = req.getRouteParam("book_id");
            String carlo = req.getRouteParam("carlo carcco");

            if (carlo == null) {
                carlo = "È null";
            }

            res.send("userId:`".concat(userId).concat("` bookId:`").concat(bookId).concat("` carlo:`").concat(carlo));
        });
    }

}
