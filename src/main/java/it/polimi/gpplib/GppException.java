package it.polimi.gpplib;

/**
 * Base exception class for all GPP (Green Public Procurement) library
 * exceptions.
 * This is a runtime exception that serves as the parent class for all
 * GPP-specific exceptions that can occur during library operations.
 * 
 * @author Politecnico di Milano
 * @version 1.0
 * @since 1.0
 */
public class GppException extends RuntimeException {

    /**
     * Constructs a new GppException with the specified detail message.
     * 
     * @param message the detail message explaining what went wrong
     */
    public GppException(String message) {
        super(message);
    }

    /**
     * Constructs a new GppException with the specified detail message and cause.
     * 
     * @param message the detail message explaining what went wrong
     * @param cause   the underlying cause of this exception
     */
    public GppException(String message, Throwable cause) {
        super(message, cause);
    }
}
