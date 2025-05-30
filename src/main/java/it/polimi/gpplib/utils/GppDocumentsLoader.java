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

/**
 * Utility class to load GPP document data from a JSON file in resources.
 */
public class GppDocumentsLoader {

    private static final String GPP_DOCS_JSON_PATH = "domain_knowledge/gpp_criteria_docs.json";

    private final ObjectMapper objectMapper;

    public GppDocumentsLoader() {
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
        // Get the InputStream for the resource file from the classpath
        // The path is relative to the classpath root (src/main/resources/)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(GPP_DOCS_JSON_PATH)) {
            // Ensure the resource was found; getResourceAsStream returns null if not
            Objects.requireNonNull(is, "Resource not found on classpath: " + GPP_DOCS_JSON_PATH);

            // Read the JSON array into a List of GppDocument objects
            // TypeReference is used here because of Java's type erasure;
            // it tells Jackson to deserialize into a List<GppDocument> not just a raw List.
            return objectMapper.readValue(is, new TypeReference<List<GppDocument>>() {
            });
        }
    }

    // example on how to load the documents
    public static void main(String[] args) {
        GppDocumentsLoader loader = new GppDocumentsLoader();
        try {
            List<GppDocument> documents = loader.loadGppDocuments();
            System.out.println("Successfully loaded " + documents.size() + " GPP documents.");
            System.out.println("First document: " + documents.get(0).getName() + " (Published: "
                    + documents.get(0).getPublicationDate() + ")");

            // You can also serialize it back to JSON to verify or for debugging:
            String jsonOutput = loader.objectMapper.writeValueAsString(documents);
            System.out.println("\n--- Reserialized JSON Output ---");
            System.out.println(jsonOutput);

        } catch (IOException e) {
            System.err.println("Error loading GPP documents: " + e.getMessage());
            e.printStackTrace();
        }
    }
}