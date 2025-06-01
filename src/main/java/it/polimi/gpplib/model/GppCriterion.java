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
    private String arg0;
    private String arg1;
    private String arg2;
    private String arg3;
    private String arg4;
    private String arg5;
    private String arg6;

    // Default constructor for Jackson
    public GppCriterion() {
    }

    // All-args constructor (optional)
    public GppCriterion(String gppDocument, String gppSource, String category, String criterionType,
            String ambitionLevel, String id, String name, List<String> relevantCpvCodes,
            String environmentalImpactType, String arg0, String arg1, String arg2, String arg3,
            String arg4, String arg5, String arg6) {
        this.gppDocument = gppDocument;
        this.gppSource = gppSource;
        this.category = category;
        this.criterionType = criterionType;
        this.ambitionLevel = ambitionLevel;
        this.id = id;
        this.name = name;
        this.relevantCpvCodes = relevantCpvCodes;
        this.environmentalImpactType = environmentalImpactType;
        this.arg0 = arg0;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
        this.arg5 = arg5;
        this.arg6 = arg6;
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

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getArg3() {
        return arg3;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public String getArg4() {
        return arg4;
    }

    public void setArg4(String arg4) {
        this.arg4 = arg4;
    }

    public String getArg5() {
        return arg5;
    }

    public void setArg5(String arg5) {
        this.arg5 = arg5;
    }

    public String getArg6() {
        return arg6;
    }

    public void setArg6(String arg6) {
        this.arg6 = arg6;
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
                ", arg0='" + arg0 + '\'' +
                ", arg1='" + arg1 + '\'' +
                ", arg2='" + arg2 + '\'' +
                ", arg3='" + arg3 + '\'' +
                ", arg4='" + arg4 + '\'' +
                ", arg5='" + arg5 + '\'' +
                ", arg6='" + arg6 + '\'' +
                '}';
    }
}
