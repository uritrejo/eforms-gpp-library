package it.polimi.gpplib.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.polimi.gpplib.model.GppPatch;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Utility class to load GPP patches from gpp_patches_data.json.
 */
public class GppPatchesLoader {

    private static final String GPP_PATCHES_JSON_PATH = "domain_knowledge/gpp_patches_data.json";
    private final ObjectMapper objectMapper;

    public GppPatchesLoader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<GppPatch> loadGppPatches() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(GPP_PATCHES_JSON_PATH)) {
            Objects.requireNonNull(is, "Resource not found on classpath: " + GPP_PATCHES_JSON_PATH);
            return objectMapper.readValue(is, new TypeReference<List<GppPatch>>() {
            });
        }
    }

    // Example main method for testing
    public static void main(String[] args) {
        GppPatchesLoader loader = new GppPatchesLoader();
        try {
            List<GppPatch> patches = loader.loadGppPatches();
            System.out.println("Successfully loaded " + patches.size() + " GPP patches.");
            if (!patches.isEmpty()) {
                System.out.println("First patch: " + patches.get(0));
            }
            // Serialize back to JSON for debugging
            String jsonOutput = loader.objectMapper.writeValueAsString(patches);
            System.out.println("\n--- Reserialized JSON Output ---");
            System.out.println(jsonOutput);
        } catch (IOException e) {
            System.err.println("Error loading GPP patches: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
