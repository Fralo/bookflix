package dev.fralo.bookflix.easyj.routing;

public abstract class Controller {
    protected final Router router;
    protected String basePath = "";

    public Controller() {
        this.router = Router.getInstance();
    }

    public void get(String path, RouteHandler handler) {
        this.router.register("GET", basePath.concat(path), handler);
    }
    public void post(String path, RouteHandler handler) {
        this.router.register("POST", basePath.concat(path), handler);
    }
    public void patch(String path, RouteHandler handler) {
        this.router.register("PATCH", basePath.concat(path), handler);
    }
    public void delete(String path, RouteHandler handler) {
        this.router.register("DELETE", basePath.concat(path), handler);
    }

    public abstract void register();
}