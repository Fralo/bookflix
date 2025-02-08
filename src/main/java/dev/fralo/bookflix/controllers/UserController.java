package dev.fralo.bookflix.controllers;

import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;
import dev.fralo.bookflix.easyj.routing.Controller;

public class UserController extends Controller{
    public UserController() {
        super();
        this.basePath = "/users";
    }

    @Override
    public void register() {
        this.get("", (Request req, Response res) -> {
            res.send(200, "SI PUO FAREEE");
        });

        this.post("/register", (Request req, Response res) -> {


            System.out.println(req.getBody());
            res.send(201, "SI PUO FAREEE");
        });
    }

    
}
