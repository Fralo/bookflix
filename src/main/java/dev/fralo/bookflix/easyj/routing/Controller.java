package dev.fralo.bookflix.easyj.routing;

public abstract class Controller {
    protected final Response response;
    protected final Request request;

    public Controller(Request request, Response response) {
        this.request = request;
        this.response = response;
    }
}