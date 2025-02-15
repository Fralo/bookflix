package dev.fralo.bookflix.easyj.core;

import java.io.IOException;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpExchange;

import dev.fralo.bookflix.easyj.auth.AuthToken;
import dev.fralo.bookflix.easyj.exceptions.RequestUnauthorizedException;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.easyj.orm.User;

/**
 * Used to represent a request that needs to be authorized in order to continue
 */
public class AuthRequest extends Request {
    private User user;


    public AuthRequest(HttpExchange exchange) throws IOException, RequestUnauthorizedException {
        super(exchange);
        this.setUser(this.authorizeUser());   
    }

    public AuthRequest(Request request) throws IOException, RequestUnauthorizedException {
        super(request);
        this.setUser(this.authorizeUser());   
    }

    private User authorizeUser() throws RequestUnauthorizedException {
        String bearerToken = this.getHeader("Authorization");
        if (bearerToken == null) {
            throw new RequestUnauthorizedException("Missing bearer token");
        }

        String tokenValue = bearerToken.substring("Bearer ".length());
        AuthToken authToken = null;
        try {
            authToken = Model.queryBuilder(AuthToken.class).where("value", tokenValue).get();
        } catch (SQLException e) {
            throw new RequestUnauthorizedException("Invalid auth token");
        }
        if (authToken == null) {
            throw new RequestUnauthorizedException("Cannot find auth token");
        }
        
        User currentUser = null;
        try {
            currentUser = Model.queryBuilder(User.class).where("id", authToken.getUserId()).get();
        } catch (SQLException e) {
            throw new RequestUnauthorizedException("No user associated with token");
        }
        if (currentUser == null) {
            throw new RequestUnauthorizedException("No user associated with token");
        }

        return currentUser;
    }

    public User getUser() {
        return this.user;
    }

    private void setUser(User user) {
        this.user = user;
    }
}
