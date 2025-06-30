package it.polimi.gpplib.utils;

import org.apache.commons.text.StringSubstitutor;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

public class GppPatchSuggester {

    private List<GppCriterion> gppCriteria = new java.util.ArrayList<>();
    private List<GppPatch> gppPatches = new java.util.ArrayList<>();

    public GppPatchSuggester(List<GppCriterion> gppCriteria, List<GppPatch> gppPatches) {
        this.gppCriteria = gppCriteria;
        this.gppPatches = gppPatches;
    }

    /**
     * Suggests GPP patches based on the provided notice and suggested criteria.
     * 
     * @param notice            the notice to analyze
     * @param suggestedCriteria the list of suggested GPP criteria
     * @return a list of suggested GPP patches
     */
    public List<SuggestedGppPatch> suggestGppPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria) {
        Map<String, List<GppCriterion>> criteriaPerLot = getCriteriaPerLot(suggestedCriteria);

        List<SuggestedGppPatch> allPatches = new java.util.ArrayList<>();
        for (String lotId : criteriaPerLot.keySet()) {
            List<GppCriterion> lotCriteria = criteriaPerLot.get(lotId);
            List<SuggestedGppPatch> lotPatches = suggestGppPatchesForLot(notice, lotId, lotCriteria);
            allPatches.addAll(lotPatches);
        }

        return allPatches;
    }

    private List<SuggestedGppPatch> suggestGppPatchesForLot(Notice notice, String lotId,
            List<GppCriterion> lotCriteria) {

        // TODO: eventually, should look at the notice to avoid or verify suggestions
        // For now, we just return everything

        List<SuggestedGppPatch> suggestedPatches = new java.util.ArrayList<>();

        List<SuggestedGppPatch> gppCriteriaSourcePatches = suggestGppCriteriaSourcePatches(lotId, lotCriteria,
                notice.getNoticeLanguage());
        suggestedPatches.addAll(gppCriteriaSourcePatches);

        List<SuggestedGppPatch> environmentalImpactPatches = suggestEnvironmentalImpactPatches(lotId, lotCriteria,
                notice.getNoticeLanguage());
        suggestedPatches.addAll(environmentalImpactPatches);

        // suggestStrategicProcurementPatches

        List<SuggestedGppPatch> strategicProcurementPatches = suggestStrategicProcurementPatches(notice, lotId,
                lotCriteria,
                notice.getNoticeLanguage());
        suggestedPatches.addAll(strategicProcurementPatches);

        // add the direct patches for each of the criteria (e.g. AC, SC, TS, CPC)
        for (GppCriterion criterion : lotCriteria) {
            List<SuggestedGppPatch> criterionPatches = suggestCriterionPatches(notice, lotId, criterion);
            for (SuggestedGppPatch patch : criterionPatches) {
                if (!suggestedPatches.contains(patch)) {
                    suggestedPatches.add(patch);
                }
            }
        }

        return suggestedPatches;
    }

    /**
     * Suggests patches to add a specific GppCriterion (AC, SC, TS, CPC).
     *
     * @param lotId     the ID of the lot
     * @param criterion the GppCriterion to suggest patches for
     * @return a list of suggested patches for the given criterion
     */
    private List<SuggestedGppPatch> suggestCriterionPatches(Notice notice, String lotId, GppCriterion criterion) {
        List<SuggestedGppPatch> patches = new java.util.ArrayList<>();

        String patchName = Constants.CRITERION_TYPE_TO_PATCH_NAME.get(criterion.getCriterionType().toLowerCase());
        if (patchName == null) {
            System.err.println("No patch name found for criterion type: " + criterion.getCriterionType());
            return patches;
        }

        GppPatch gppPatch = findGppPatchByName(patchName);
        if (gppPatch == null) {
            System.err.println("GPP Patch not found: " + patchName);
            return patches;
        }

        // TODO: eventually, the criterion should also account for the notice language
        Map<String, String> variables = buildPatchVariables(criterion, notice.getNoticeLanguage());

        String parsedValue = parseValue(gppPatch.getValue(), variables);
        String description = String.format(
                "Adds the criterion: { Type: %s, Name: %s, ID: %s, Ambition Level: %s, Document: %s }",
                criterion.getCriterionType(),
                criterion.getName(),
                criterion.getId(),
                criterion.getAmbitionLevel(),
                criterion.getGppDocument());
        SuggestedGppPatch suggestedPatch = new SuggestedGppPatch(
                criterion.getCriterionType() + " --- " + criterion.getId() + ": " + criterion.getName(),
                gppPatch.getBtIds(),
                gppPatch.getDependsOn(),
                gppPatch.getPathInLot(),
                parsedValue,
                Constants.OP_CREATE,
                description,
                lotId);
        patches.add(suggestedPatch);

        // Add parent patches if needed (to build the structure)
        List<SuggestedGppPatch> parentPatches = buildParentPatches(notice, lotId, gppPatch, variables, patchName);
        patches.addAll(0, parentPatches);

        return patches;
    }

    // ??++
    /**
     * Builds the variable map for patch value substitution from a GppCriterion.
     * The mapping of this depends on the arguments included in the GppPatch data
     * domain knowledge file.
     */
    private Map<String, String> buildPatchVariables(GppCriterion criterion, String language) {
        Map<String, String> variables = new HashMap<>(Constants.NAMESPACE_MAP);

        if (language == null || language.isEmpty()) {
            language = Constants.TAG_ENGLISH; // Default to English if not provided
        }

        variables.put(Constants.TAG_LANGUAGE, language);

        switch (criterion.getCriterionType().toLowerCase()) {
            case Constants.CRITERION_TYPE_TECHNICAL_SPECIFICATION:
                // For now, technical specifications will go in the same patch as award criteria
            case Constants.CRITERION_TYPE_AWARD_CRITERIA:
                String formattedName = String.format(
                        "GPP Award Criterion [ID: %s, Name: %s, Ambition Level: %s, GPP Document: %s]",
                        criterion.getId(),
                        criterion.getName(),
                        criterion.getFormattedAmbitionLevel(),
                        criterion.getGppDocument());

                // TODO: this has to be dynamic according to the other award criteria in the
                // notice!!!
                // TODO: Maybe just remove them or add a minimum or something similar
                // since you send the patch suggestions before even knowing how many will be
                // added
                variables.put(Constants.TAG_ARG0, "number-weight");
                variables.put(Constants.TAG_ARG1, "per-exa");
                variables.put(Constants.TAG_ARG2, "100");
                variables.put(Constants.TAG_ARG3, Constants.AWARD_CRITERIA_TYPE_QUALITY);
                variables.put(Constants.TAG_ARG4, formattedName);
                variables.put(Constants.TAG_ARG5, criterion.getDescription());
                break;
            case Constants.CRITERION_TYPE_SELECTION_CRITERIA:
                formattedName = String.format(
                        "GPP Select Criterion [ID: %s, Name: %s, Ambition Level: %s, GPP Document: %s]",
                        criterion.getId(),
                        criterion.getName(),
                        criterion.getFormattedAmbitionLevel(),
                        criterion.getGppDocument());
                String formattedDescription = String.format(
                        "%s --- Extended Description: %s", formattedName, criterion.getDescription());
                String tendererReqTypeCode = criterion.getSelectionCriterionType() != null
                        ? criterion.getSelectionCriterionType()
                        : Constants.TENDERER_REQ_CODE_ENV_MANAGEMENT;
                variables.put(Constants.TAG_ARG0, tendererReqTypeCode);
                variables.put(Constants.TAG_ARG1, formattedDescription);
                break;
            case Constants.CRITERION_TYPE_CONTRACT_PERFORMANCE_CLAUSE:
                formattedName = String.format(
                        "GPP Contract Performance Clause [ID: %s, Name: %s, Ambition Level: %s, GPP Document: %s]",
                        criterion.getId(),
                        criterion.getName(),
                        criterion.getFormattedAmbitionLevel(),
                        criterion.getGppDocument());
                formattedDescription = String.format(
                        "%s --- Extended Description: %s", formattedName, criterion.getDescription());
                variables.put(Constants.TAG_ARG0, formattedDescription);
                break;
            default:
                break;
        }

        return variables;
    }

    /**
     * Builds a list of parent patches required for the given patch if the path does
     * not exist in the notice.
     */
    private List<SuggestedGppPatch> buildParentPatches(Notice notice, String lotId, GppPatch gppPatch,
            Map<String, String> variables, String patchName) {
        List<SuggestedGppPatch> parentPatches = new java.util.ArrayList<>();
        GppPatch currentPatch = gppPatch;

        while (!notice.doesPathExistInLot(lotId, currentPatch.getPathInLot())) {
            String parentPatchName = currentPatch.getDependsOn();
            if (parentPatchName == null || parentPatchName.isEmpty() || parentPatchName.equals("-")) {
                System.err.println("No parent patch defined for: " + currentPatch.getName());
                break;
            }
            GppPatch parentPatch = findGppPatchByName(parentPatchName);
            if (parentPatch == null) {
                System.err.println("Parent Patch not found: " + parentPatchName);
                break;
            }

            SuggestedGppPatch parentSuggestedPatch = new SuggestedGppPatch(
                    parentPatch.getName(),
                    parentPatch.getBtIds(),
                    parentPatch.getDependsOn(),
                    parentPatch.getPathInLot(),
                    parseValue(parentPatch.getValue(), variables),
                    Constants.OP_CREATE,
                    "Parent structure for: " + patchName,
                    lotId);
            parentPatches.add(0, parentSuggestedPatch);
            currentPatch = parentPatch;
        }

        return parentPatches;
    }

    private List<SuggestedGppPatch> suggestGppCriteriaSourcePatches(String lotId,
            List<GppCriterion> lotCriteria, String language) {
        List<SuggestedGppPatch> patches = new java.util.ArrayList<>();
        GppPatch gppCriteriaPatch = findGppPatchByName(Constants.PATCH_NAME_GPP_CRITERIA_SOURCE);
        if (gppCriteriaPatch == null) {
            System.err.println("GPP Criteria Patch not found: " + Constants.PATCH_NAME_GPP_CRITERIA_SOURCE);
            return patches; // No patches to suggest
        }

        if (language == null || language.isEmpty()) {
            language = Constants.TAG_ENGLISH; // Default to English if not provided
        }

        List<String> gppSources = getGppSources(lotCriteria);
        for (String source : gppSources) {
            Map<String, String> variables = new HashMap<>(Constants.NAMESPACE_MAP);
            variables.put(Constants.TAG_LANGUAGE, language);
            variables.put(Constants.TAG_ARG0, source);
            String parsedValue = parseValue(gppCriteriaPatch.getValue(), variables);

            SuggestedGppPatch patch = new SuggestedGppPatch(
                    Constants.PATCH_NAME_GPP_CRITERIA_SOURCE + " - " + source,
                    gppCriteriaPatch.getBtIds(),
                    gppCriteriaPatch.getDependsOn(),
                    gppCriteriaPatch.getPathInLot(),
                    parsedValue,
                    Constants.OP_CREATE,
                    "Indicates the usage of GPP criteria", // Placeholder description
                    lotId);
            patches.add(patch);
        }
        return patches;
    }

    private List<SuggestedGppPatch> suggestEnvironmentalImpactPatches(String lotId,
            List<GppCriterion> lotCriteria, String language) {
        List<SuggestedGppPatch> patches = new java.util.ArrayList<>();
        GppPatch envImpactPatch = findGppPatchByName(Constants.PATCH_NAME_ENVIRONMENTAL_IMPACT);
        if (envImpactPatch == null) {
            System.err.println("Green Procurement Patch not found: " + Constants.PATCH_NAME_ENVIRONMENTAL_IMPACT);
            return patches; // No patches to suggest
        }

        if (language == null || language.isEmpty()) {
            language = Constants.TAG_ENGLISH; // Default to English if not provided
        }

        List<String> environmentalImpacts = getEnvironmentalImpacts(lotCriteria);
        for (String impact : environmentalImpacts) {
            Map<String, String> variables = new HashMap<>(Constants.NAMESPACE_MAP);
            variables.put(Constants.TAG_LANGUAGE, language);
            variables.put(Constants.TAG_ARG0, impact);
            String parsedValue = parseValue(envImpactPatch.getValue(), variables);
            SuggestedGppPatch patch = new SuggestedGppPatch(
                    Constants.PATCH_NAME_ENVIRONMENTAL_IMPACT + " - " + impact,
                    envImpactPatch.getBtIds(),
                    envImpactPatch.getDependsOn(),
                    envImpactPatch.getPathInLot(),
                    parsedValue,
                    Constants.OP_CREATE,
                    "Indicates an approach to reducing the environmental impacts of the work, supply or service", // Placeholder
                    lotId);
            patches.add(patch);
        }

        return patches;
    }

    private List<SuggestedGppPatch> suggestStrategicProcurementPatches(Notice notice, String lotId,
            List<GppCriterion> lotCriteria, String language) {

        List<SuggestedGppPatch> patches = new java.util.ArrayList<>();
        Node lot = notice.getLotNode(lotId);

        if (!XmlUtils.doesNodeExistAtPath(lot, Constants.PATH_STRATEGIC_PROCUREMENT_ENV_IMP)) {
            patches.add(createStrategicProcurementForEnvImpPatch(lotId, lotCriteria, notice.getNoticeLanguage()));
        }

        if (XmlUtils.doesNodeExistAtPath(lot, Constants.PATH_STRATEGIC_PROCUREMENT_NONE)) {
            patches.add(createRemovalPatchStrategicProcurementNone(lotId));
        }

        return patches;
    }

    private SuggestedGppPatch createStrategicProcurementForEnvImpPatch(String lotId,
            List<GppCriterion> lotCriteria, String language) {
        GppPatch envImpactPatch = findGppPatchByName(Constants.PATCH_NAME_STRATEGIC_PROCUREMENT_ENV_IMP);
        if (envImpactPatch == null) {
            System.err.println(
                    "Strategic Procurement Patch not found: " + Constants.PATCH_NAME_STRATEGIC_PROCUREMENT_ENV_IMP);
            return null;
        }

        if (language == null || language.isEmpty()) {
            language = Constants.TAG_ENGLISH; // Default to English if not provided
        }

        Map<String, String> variables = new HashMap<>(Constants.NAMESPACE_MAP);
        variables.put(Constants.TAG_LANGUAGE, language);
        variables.put(Constants.TAG_ARG0, Constants.PATCH_DESCRIPTION_STRATEGIC_PROCUREMENT);
        String parsedValue = parseValue(envImpactPatch.getValue(), variables);
        return new SuggestedGppPatch(
                Constants.PATCH_NAME_STRATEGIC_PROCUREMENT_ENV_IMP,
                envImpactPatch.getBtIds(),
                envImpactPatch.getDependsOn(),
                envImpactPatch.getPathInLot(),
                parsedValue,
                Constants.OP_CREATE,
                "Indicates a strategic procurement for the reduction of environmental impacts", // Placeholder
                lotId);
    }

    private SuggestedGppPatch createRemovalPatchStrategicProcurementNone(String lotId) {
        SuggestedGppPatch patch = new SuggestedGppPatch(
                "Removal of: " + Constants.PATCH_NAME_STRATEGIC_PROCUREMENT_NONE,
                List.of(),
                "",
                Constants.PATH_STRATEGIC_PROCUREMENT_NONE,
                "",
                Constants.OP_REMOVE,
                "Removes the indication that no strategic procurement is used for this notice",
                lotId);
        return patch;
    }

    private GppPatch findGppPatchByName(String name) {
        for (GppPatch patch : gppPatches) {
            if (patch.getName().equalsIgnoreCase(name)) {
                return patch;
            }
        }
        return null; // Not found
    }

    private List<String> getGppSources(List<GppCriterion> criteria) {
        List<String> sources = new java.util.ArrayList<>();
        for (GppCriterion criterion : criteria) {
            String source = criterion.getGppSource();
            if (source != null && !sources.contains(source)) {
                sources.add(source);
            }
        }
        return sources;
    }

    private List<String> getEnvironmentalImpacts(List<GppCriterion> criteria) {
        List<String> environmentalImpacts = new java.util.ArrayList<>();
        for (GppCriterion criterion : criteria) {
            String environmentalImpact = criterion.getEnvironmentalImpactType();
            if (environmentalImpact != null && !environmentalImpacts.contains(environmentalImpact)) {
                environmentalImpacts.add(environmentalImpact);
            }
        }
        return environmentalImpacts;
    }

    /**
     * Returns the corresponding GppCriterion that contains all the internal details
     * for a given SuggestedGppCriterion, or null if not found.
     *
     * @param suggestedCriterion the suggested criterion to match
     * @return the matching GppCriterion, or null if not found
     */
    private GppCriterion convertToGppCriterion(SuggestedGppCriterion suggestedCriterion) {
        for (GppCriterion criterion : gppCriteria) {
            boolean idMatch = suggestedCriterion.getId() != null
                    && suggestedCriterion.getId().equalsIgnoreCase(criterion.getId());
            boolean ambitionMatch = suggestedCriterion.getAmbitionLevel() != null
                    && suggestedCriterion.getAmbitionLevel().equalsIgnoreCase(criterion.getAmbitionLevel());
            boolean docMatch = suggestedCriterion.getGppDocument() != null
                    && suggestedCriterion.getGppDocument().equalsIgnoreCase(criterion.getGppDocument());
            if (idMatch && ambitionMatch && docMatch) {
                return criterion;
            }
        }
        return null;
    }

    /**
     * Groups GppCriterion objects by lotId, based on the provided list of
     * SuggestedGppCriterion.
     * The returned map has lotId as key and the list of matching GppCriterion as
     * value.
     */
    private Map<String, List<GppCriterion>> getCriteriaPerLot(List<SuggestedGppCriterion> suggestedCriteria) {
        Map<String, List<GppCriterion>> criteriaPerLot = new HashMap<>();
        for (SuggestedGppCriterion suggested : suggestedCriteria) {
            String lotId = suggested.getLotId();
            GppCriterion gppCriterion = convertToGppCriterion(suggested);
            if (gppCriterion == null) {
                System.err.println("No matching GppCriterion found for suggested criterion: " + suggested);
                continue;
            }
            criteriaPerLot.computeIfAbsent(lotId, k -> new java.util.ArrayList<>()).add(gppCriterion);
        }
        return criteriaPerLot;
    }

    private String parseValue(String value, Map<String, String> variables) {
        if (value == null || value.isEmpty()) {
            return value; // No parsing needed
        }
        StringSubstitutor sub = new StringSubstitutor(variables, "{", "}");
        return sub.replace(value);
    }

}
