package dev.fralo.bookflix.controllers;

import java.util.List;

import dev.fralo.bookflix.easyj.annotations.router.Get;
import dev.fralo.bookflix.easyj.annotations.router.PathParam;
import dev.fralo.bookflix.easyj.annotations.router.Post;
import dev.fralo.bookflix.easyj.routing.Controller;
import dev.fralo.bookflix.easyj.routing.Request;
import dev.fralo.bookflix.easyj.routing.Response;

public class UserController extends Controller {
    public UserController(Request request, Response response) {
        super(request, response);
    }

    @Get("/users")
    public void listUsers() throws Exception {
        response.json(List.of("User1", "User2"));
    }

    @Get("/users/{id}")
    public void getUser(@PathParam("id") String userId) throws Exception {
        response.send("User ID: " + userId);
    }

    @Post("/users")
    public void createUser() throws Exception {
        // Implementation
        response.send(201, "User created");
    }
}
