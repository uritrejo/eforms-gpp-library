package it.polimi.gpplib;

/**
 * Exception thrown when a request to the GPP library contains invalid
 * parameters or data.
 * This typically indicates that the client has provided malformed input,
 * invalid file paths,
 * or other incorrect parameters that prevent the library from processing the
 * request.
 * 
 * @author Politecnico di Milano
 * @version 1.0
 * @since 1.0
 */
public class GppBadRequestException extends GppException {

    /**
     * Constructs a new GppBadRequestException with the specified detail message.
     * 
     * @param message the detail message explaining what was wrong with the request
     */
    public GppBadRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new GppBadRequestException with the specified detail message and
     * cause.
     * 
     * @param message the detail message explaining what was wrong with the request
     * @param cause   the underlying cause of this exception
     */
    public GppBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}