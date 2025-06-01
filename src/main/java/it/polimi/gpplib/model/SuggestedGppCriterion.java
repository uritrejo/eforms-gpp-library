package it.polimi.gpplib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Represents a suggested GPP criterion, based on GppCriterion but with a
 * reduced set of fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestedGppCriterion {
    private String gppDocument;
    private String category;
    private String criterionType;
    private String ambitionLevel;
    private String id;
    private String name;
    private List<String> relevantCpvCodes;
    private List<String> matchingCpvCodes;
    private String lotId;

    public SuggestedGppCriterion() {
    }

    // TODO: could later add a description maybe? would need to be in the
    // sheets/json as well
    public SuggestedGppCriterion(String gppDocument, String category, String criterionType, String ambitionLevel,
            String id, String name, List<String> relevantCpvCodes, List<String> matchingCpvCodes, String lotId) {
        this.gppDocument = gppDocument;
        this.category = category;
        this.criterionType = criterionType;
        this.ambitionLevel = ambitionLevel;
        this.id = id;
        this.name = name;
        this.relevantCpvCodes = relevantCpvCodes;
        this.matchingCpvCodes = matchingCpvCodes;
        this.lotId = lotId;
    }

    public String getGppDocument() {
        return gppDocument;
    }

    public void setGppDocument(String gppDocument) {
        this.gppDocument = gppDocument;
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

    public List<String> getMatchingCpvCodes() {
        return matchingCpvCodes;
    }

    public void setMatchingCpvCodes(List<String> matchingCpvCodes) {
        this.matchingCpvCodes = matchingCpvCodes;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    @Override
    public String toString() {
        return "SuggestedGppCriterion{" +
                "gppDocument='" + gppDocument + '\'' +
                ", category='" + category + '\'' +
                ", criterionType='" + criterionType + '\'' +
                ", ambitionLevel='" + ambitionLevel + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", relevantCpvCodes=" + relevantCpvCodes +
                ", matchingCpvCodes=" + matchingCpvCodes +
                ", lotId='" + lotId + '\'' +
                '}';
    }
}
