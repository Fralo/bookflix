package dev.fralo.bookflix.easyj.routing;



import dev.fralo.bookflix.easyj.core.Request;
import dev.fralo.bookflix.easyj.core.Response;

@FunctionalInterface
public interface RouteHandler {
    void handle(Request request, Response response) throws Exception;
}
