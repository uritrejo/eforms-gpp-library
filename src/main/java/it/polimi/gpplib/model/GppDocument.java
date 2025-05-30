package it.polimi.gpplib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a Green Public Procurement (GPP) document from the JSON resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Don't include fields with null values in JSON output
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Maps JSON
// snake_case to Java camelCase
public class GppDocument {
    private String name;
    private String source;
    private String documentReference;
    private LocalDateTime publicationDate; // Jackson can parse "YYYY-MM-DDTHH:mm:ss" into this
    private List<String> relevantCpvCodes;
    private String summary;

    // --- Constructors ---
    public GppDocument() {
        // Default constructor for Jackson deserialization
    }

    public GppDocument(String name, String source, String documentReference, LocalDateTime publicationDate,
            List<String> relevantCpvCodes, String summary) {
        this.name = name;
        this.source = source;
        this.documentReference = documentReference;
        this.publicationDate = publicationDate;
        this.relevantCpvCodes = relevantCpvCodes;
        this.summary = summary;
    }

    // --- Getters and Setters (REQUIRED for Jackson) ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<String> getRelevantCpvCodes() {
        return relevantCpvCodes;
    }

    public void setRelevantCpvCodes(List<String> relevantCpvCodes) {
        this.relevantCpvCodes = relevantCpvCodes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "GppDocument{" +
                "name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", publicationDate=" + publicationDate +
                ", relevantCpvCodes=" + relevantCpvCodes +
                '}';
    }
}