package dev.fralo.bookflix.easyj.routing;



import dev.fralo.bookflix.core.Request;
import dev.fralo.bookflix.core.Response;

@FunctionalInterface
public interface RouteHandler {
    void handle(Request request, Response response) throws Exception;
}
