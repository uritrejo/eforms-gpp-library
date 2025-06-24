package it.polimi.gpplib;

public class GppBadRequestException extends GppException {
    public GppBadRequestException(String message) {
        super(message);
    }

    public GppBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}