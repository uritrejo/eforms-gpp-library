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
    private final String filePath;
    private final ObjectMapper objectMapper;

    public GppPatchesLoader(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<GppPatch> loadGppPatches() throws IOException {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(filePath)) {
            Objects.requireNonNull(is,
                    "Resource not found on classpath: " + filePath);
            return objectMapper.readValue(is, new TypeReference<List<GppPatch>>() {
            });
        }
    }
}
