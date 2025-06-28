package it.polimi.gpplib;

/**
 * Exception thrown when an internal error occurs within the GPP library.
 * This typically indicates an unexpected system error or configuration issue
 * that prevents the library from functioning properly.
 * 
 * @author Politecnico di Milano
 * @version 1.0
 * @since 1.0
 */
public class GppInternalErrorException extends GppException {

    /**
     * Constructs a new GppInternalErrorException with the specified detail message.
     * 
     * @param message the detail message explaining the internal error
     */
    public GppInternalErrorException(String message) {
        super(message);
    }

    /**
     * Constructs a new GppInternalErrorException with the specified detail message
     * and cause.
     * 
     * @param message the detail message explaining the internal error
     * @param cause   the cause of this exception
     */
    public GppInternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}