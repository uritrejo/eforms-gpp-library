package it.polimi.gpplib;

public class GppException extends RuntimeException {
    public GppException(String message) {
        super(message);
    }

    public GppException(String message, Throwable cause) {
        super(message, cause);
    }
}
