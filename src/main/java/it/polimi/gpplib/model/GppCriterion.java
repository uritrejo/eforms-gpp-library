package it.polimi.gpplib.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.polimi.gpplib.utils.Utils;

import java.util.List;

/**
 * Represents a GPP Criterion from the gpp_criteria.json resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GppCriterion {
    public static final String AMBITION_LEVEL_CORE = "core";
    public static final String AMBITION_LEVEL_COMPREHENSIVE = "comprehensive";
    public static final String AMBITION_LEVEL_BOTH = "both";

    private String gppDocument;
    private String gppSource;
    private String category;
    private String criterionType;
    private String ambitionLevel;
    private String id;
    private String name;
    private List<String> relevantCpvCodes;
    private String environmentalImpactType;
    private String description;
    private String selectionCriterionType;

    // Default constructor for Jackson
    public GppCriterion() {
    }

    // All-args constructor (optional)
    public GppCriterion(String gppDocument, String gppSource, String category, String criterionType,
            String ambitionLevel, String id, String name, List<String> relevantCpvCodes,
            String environmentalImpactType, String description, String selectionCriterionType) {
        this.gppDocument = gppDocument;
        this.gppSource = gppSource;
        this.category = category;
        this.criterionType = criterionType;
        this.ambitionLevel = ambitionLevel;
        this.id = id;
        this.name = name;
        this.relevantCpvCodes = relevantCpvCodes;
        this.environmentalImpactType = environmentalImpactType;
        this.description = description;
        this.selectionCriterionType = selectionCriterionType;
    }

    // Getters and setters
    public String getGppDocument() {
        return gppDocument;
    }

    public void setGppDocument(String gppDocument) {
        this.gppDocument = gppDocument;
    }

    public String getGppSource() {
        return gppSource;
    }

    public void setGppSource(String gppSource) {
        this.gppSource = gppSource;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCriterionType() {
        return criterionType;
    }

    public void setCriterionType(String criterionType) {
        this.criterionType = criterionType;
    }

    public String getAmbitionLevel() {
        return ambitionLevel;
    }

    public String getFormattedAmbitionLevel() {
        if (ambitionLevel.equalsIgnoreCase(Constants.AMBITION_LEVEL_BOTH)) {
            return "core and comprehensive";
        }
        return ambitionLevel;
    }

    public void setAmbitionLevel(String ambitionLevel) {
        this.ambitionLevel = ambitionLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRelevantCpvCodes() {
        return relevantCpvCodes;
    }

    public void setRelevantCpvCodes(List<String> relevantCpvCodes) {
        this.relevantCpvCodes = relevantCpvCodes;
    }

    public String getEnvironmentalImpactType() {
        return environmentalImpactType;
    }

    public void setEnvironmentalImpactType(String environmentalImpactType) {
        this.environmentalImpactType = environmentalImpactType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSelectionCriterionType() {
        return selectionCriterionType;
    }

    public void setSelectionCriterionType(String selectionCriterionType) {
        this.selectionCriterionType = selectionCriterionType;
    }

    public boolean isApplicable(List<String> cpvCodes, String ambitionLevel) {
        // Check CPV match
        boolean cpvMatch = Utils.hasMatchingCpvs(cpvCodes, relevantCpvCodes);
        // Check ambition level match (case-insensitive, allow "both")
        boolean ambitionMatch = this.ambitionLevel.toLowerCase().equals("both")
                || this.ambitionLevel.toLowerCase().equals(ambitionLevel);
        return cpvMatch && ambitionMatch;
    }

    @Override
    public String toString() {
        return "GppCriterion{" +
                "gppDocument='" + gppDocument + '\'' +
                ", gppSource='" + gppSource + '\'' +
                ", category='" + category + '\'' +
                ", criterionType='" + criterionType + '\'' +
                ", ambitionLevel='" + ambitionLevel + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", relevantCpvCodes=" + relevantCpvCodes +
                ", environmentalImpactType='" + environmentalImpactType + '\'' +
                ", description='" + description + '\'' +
                ", selectionCriterionType='" + selectionCriterionType + '\'' +
                '}';
    }
}
