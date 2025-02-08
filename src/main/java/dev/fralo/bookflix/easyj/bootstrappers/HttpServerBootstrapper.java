package dev.fralo.bookflix.easyj.bootstrappers;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import dev.fralo.bookflix.easyj.core.RequestHandler;

public class HttpServerBootstrapper extends Bootstrapper {

    private final int PORT = 8000;

    @Override
    public void bootstrap() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new RequestHandler());

        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running on port " + String.valueOf(PORT));
    }
}
