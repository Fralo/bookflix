package dev.fralo.bookflix.controllers;

import java.util.List;

import dev.fralo.bookflix.easyj.auth.User;
import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.easyj.routing.Controller;
import dev.fralo.bookflix.models.Book;
import dev.fralo.bookflix.requests.CreateBookRequest;

public class BookController extends Controller {
    public BookController() {
        super();
        this.basePath = "/books";
    }

    @Override
    public void register() {
        this.get("", (Request req, Response res) -> {
            User user = req.getUser();

            List<Book> userBooks = Model.queryBuilder(Book.class).where("user_id", user.getId()).all();

            res.json(userBooks);
        });

        this.get("/{id}", (Request req, Response res) -> {
            User user = req.getUser();

            Book book = Model.queryBuilder(Book.class)
                .where("user_id", user.getId())
                .where("id", req.getRouteParamInt("id")).get();

            if (book == null) {
                res.send(404, "Not found");
                return;
            }

            res.json(book);
        });

        this.post("", (Request req, Response res) -> {
            User user = req.getUser();

            CreateBookRequest bookData = req.getBody(CreateBookRequest.class);
            
            System.out.println(user.getId());
            if(user.getId() != bookData.user_id) {
                throw new Exception("Cannot add a book to another user");
            }

            Book book = new Book(bookData.user_id, bookData.author, bookData.title);
            book.save();
            res.json(book, 201);
        });

        this.patch("/{id}/complete", (Request req, Response res) -> {
            User user = req.getUser();

            Book book = Model.queryBuilder(Book.class)
                .where("user_id", user.getId())
                .where("id", req.getRouteParamInt("id")).get();

            if (book == null) {
                res.send(404, "Not found");
                return;
            }

            book.setCompleted(true);
            book.save();

            res.json(book);
        });
    }
}