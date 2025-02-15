package dev.fralo.bookflix.easyj.responses;

public class ErrorResponse {
    public String message;
    private int status;
    public ErrorResponse(String message) {
        this.message = message;
        this.status = 400;
    }
    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }
    public int getStatus()  {
        return this.status;
    }
}