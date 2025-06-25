package it.polimi.gpplib.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.GppCriterion;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Utility class to load GPP criteria data from a JSON file in resources.
 */
public class GppCriteriaLoader {

    private final String filePath;
    private final ObjectMapper objectMapper;

    public GppCriteriaLoader(String filePath) {
        this.filePath = filePath;
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
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(filePath)) {
            Objects.requireNonNull(is,
                    "Resource not found on classpath: " + filePath);
            return objectMapper.readValue(is, new TypeReference<List<GppCriterion>>() {
            });
        }
    }
}
