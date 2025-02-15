package dev.fralo.bookflix.easyj.core;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import dev.fralo.bookflix.easyj.responses.ErrorResponse;
import dev.fralo.bookflix.easyj.routing.Router;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Request request = new Request(exchange);
            Response response = new Response(exchange);

            Router router = Router.getInstance();

            router.handle(request, response);
        } catch (Exception e) {
            e.printStackTrace();

            Response r = new Response(exchange);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            r.json(errorResponse, errorResponse.getStatus());
        }
    }
}