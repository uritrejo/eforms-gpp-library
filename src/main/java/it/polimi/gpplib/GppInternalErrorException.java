package it.polimi.gpplib;

public class GppInternalErrorException extends GppException {
    public GppInternalErrorException(String message) {
        super(message);
    }

    public GppInternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}