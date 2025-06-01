package it.polimi.gpplib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GppAnalysisResult {

    private List<GppDocument> relevantGppDocuments;
    private List<SuggestedGppCriterion> suggestedGppCriteria;

    public GppAnalysisResult() {
        // Default constructor for Jackson deserialization
    }

    public GppAnalysisResult(List<GppDocument> relevantGppDocuments,
            List<SuggestedGppCriterion> suggestedGppCriteria) {
        this.relevantGppDocuments = relevantGppDocuments;
        this.suggestedGppCriteria = suggestedGppCriteria;
    }

    public List<GppDocument> getRelevantGppDocuments() {
        return relevantGppDocuments;
    }

    public void setRelevantGppDocuments(List<GppDocument> relevantGppDocuments) {
        this.relevantGppDocuments = relevantGppDocuments;
    }

    public List<SuggestedGppCriterion> getSuggestedGppCriteria() {
        return suggestedGppCriteria;
    }

    public void setSuggestedGppCriteria(List<SuggestedGppCriterion> suggestedGppCriteria) {
        this.suggestedGppCriteria = suggestedGppCriteria;
    }
}
