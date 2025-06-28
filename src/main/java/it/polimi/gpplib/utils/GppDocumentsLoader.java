package it.polimi.gpplib.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Important for java.time types like OffsetDateTime
import com.fasterxml.jackson.core.type.TypeReference; // Essential for deserializing Lists

import it.polimi.gpplib.model.GppDocument; // Your GppDocument POJO

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects; // For Objects.requireNonNull
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to load GPP document data from a JSON file in resources.
 */
public class GppDocumentsLoader {

    private static final Logger logger = LoggerFactory.getLogger(GppDocumentsLoader.class);

    private final ObjectMapper objectMapper;
    private final String filePath;

    public GppDocumentsLoader(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        // Configure ObjectMapper for pretty printing and handling java.time types
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure dates are written as ISO
                                                                                   // strings
        this.objectMapper.registerModule(new JavaTimeModule()); // Register module for java.time types
        // @JsonNaming annotation on POJOs handles snake_case to camelCase mapping
    }

    /**
     * Loads a list of GppDocument objects from the specified JSON resource file.
     *
     * @return A List of GppDocument objects.
     * @throws IOException if the resource is not found or there's an error during
     *                     JSON parsing.
     */
    public List<GppDocument> loadGppDocuments() throws IOException {
        logger.debug("Loading GPP documents from resource: {}", filePath);

        // Get the InputStream for the resource file from the classpath
        // The path is relative to the classpath root (src/main/resources/)
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(filePath)) {
            // Ensure the resource was found; getResourceAsStream returns null if not
            Objects.requireNonNull(is, "Resource not found on classpath: " + filePath);

            // Read the JSON array into a List of GppDocument objects
            // TypeReference is used here because of Java's type erasure;
            // it tells Jackson to deserialize into a List<GppDocument> not just a raw List.
            List<GppDocument> documents = objectMapper.readValue(is, new TypeReference<List<GppDocument>>() {
            });

            logger.debug("Successfully loaded {} GPP documents from {}", documents.size(), filePath);
            return documents;
        } catch (IOException e) {
            logger.error("Failed to load GPP documents from resource: {}", filePath, e);
            throw e;
        }
    }
}