package it.polimi.gpplib.utils;

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

        GppPatch gppCriteriaPatch = findGppPatchByName(Constants.PATCH_NAME_GPP_CRITERIA);
        List<String> gppSources = getGppSources(lotCriteria);
        for (String source : gppSources) {
            String parsedValue = "dummy-value" + source;
            // ??++ AQUI: parse the value from the gppCriteriaPatch
            // Create a patch for each GPP source
            SuggestedGppPatch gppSourcePatch = new SuggestedGppPatch(
                    Constants.PATCH_NAME_GPP_CRITERIA,
                    gppCriteriaPatch.getBtIds(),
                    gppCriteriaPatch.getDependsOn(),
                    gppCriteriaPatch.getPathInLot(),
                    parsedValue,
                    Constants.OP_CREATE,
                    // TODO: should add a description to the patches sheet
                    "Dummy Description for Green Public Procurement Criteria" // Placeholder description
            );
            suggestedPatches.add(gppSourcePatch);
        }

        return suggestedPatches;
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

}
