package it.polimi.gpplib.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import it.polimi.gpplib.model.GppCriterion;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Utility class to load GPP criteria data from a JSON file in resources.
 */
public class GppCriteriaLoader {

    private static final String GPP_CRITERIA_JSON_PATH = "domain_knowledge/gpp_criteria.json";

    private final ObjectMapper objectMapper;

    public GppCriteriaLoader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Loads a list of GppCriterion objects from the specified JSON resource file.
     *
     * @return A List of GppCriterion objects.
     * @throws IOException if the resource is not found or there's an error during
     *                     JSON parsing.
     */
    public List<GppCriterion> loadGppCriteria() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(GPP_CRITERIA_JSON_PATH)) {
            Objects.requireNonNull(is, "Resource not found on classpath: " + GPP_CRITERIA_JSON_PATH);
            return objectMapper.readValue(is, new TypeReference<List<GppCriterion>>() {
            });
        }
    }

    // Example main method for testing
    public static void main(String[] args) {
        GppCriteriaLoader loader = new GppCriteriaLoader();
        try {
            List<GppCriterion> criteria = loader.loadGppCriteria();
            System.out.println("Successfully loaded " + criteria.size() + " GPP criteria.");
            if (!criteria.isEmpty()) {
                System.out.println("First criterion: " + criteria.get(0));
            }

            // Serialize back to JSON for debugging
            String jsonOutput = loader.objectMapper.writeValueAsString(criteria);
            System.out.println("\n--- Reserialized JSON Output ---");
            // ctx: the output doesn't show the args, but it's okay, they are in the object
            System.out.println(jsonOutput);
        } catch (IOException e) {
            System.err.println("Error loading GPP criteria: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
