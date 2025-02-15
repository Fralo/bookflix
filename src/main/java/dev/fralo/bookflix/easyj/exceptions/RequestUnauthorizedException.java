package dev.fralo.bookflix.easyj.exceptions;

public class RequestUnauthorizedException extends Exception {
    public RequestUnauthorizedException(String messageString) {
        super(messageString);
    }
}
