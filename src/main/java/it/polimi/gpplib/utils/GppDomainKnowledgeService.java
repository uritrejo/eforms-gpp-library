package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.List;

public class GppDomainKnowledgeService {

    private List<GppDocument> gppDocs = new java.util.ArrayList<>();
    private List<GppCriterion> gppCriteria = new java.util.ArrayList<>();
    private List<GppPatch> gppPatches = new java.util.ArrayList<>();

    public GppDomainKnowledgeService() {
        try {
            GppDocumentsLoader docsLoader = new GppDocumentsLoader();
            gppDocs = docsLoader.loadGppDocuments();

            GppCriteriaLoader criteriaLoader = new GppCriteriaLoader();
            gppCriteria = criteriaLoader.loadGppCriteria();

            GppPatchesLoader patchesLoader = new GppPatchesLoader();
            gppPatches = patchesLoader.loadGppPatches();
        } catch (Exception e) {
            System.err.println("Failed to load GPP domain knowledge: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO: eventually, the relevant documents should only come from the relevant
    // GPP criteria (looking at the document names)
    public List<GppDocument> getRelevantGppDocuments(List<String> cpvs) {
        List<GppDocument> relevantGppDocs = new java.util.ArrayList<>();
        for (GppDocument doc : gppDocs) {
            if (doc.isApplicable(cpvs)) {
                relevantGppDocs.add(doc);
            }
        }
        return relevantGppDocs;
    }

    public List<GppCriterion> getRelevantGppCriteria(List<String> cpvs, String ambitionLevel) {
        List<GppCriterion> relevantGppCriteria = new java.util.ArrayList<>();
        for (GppCriterion criterion : gppCriteria) {
            if (criterion.isApplicable(cpvs, ambitionLevel)) {
                relevantGppCriteria.add(criterion);
            }
        }
        return relevantGppCriteria;
    }

    /**
     * Converts a list of GppCriterion to SuggestedGppCriterion for a given lot.
     * The matching CPVs are computed for each criterion.
     */
    public List<SuggestedGppCriterion> convertToSuggestedGppCriteria(List<GppCriterion> criteria, String lotId,
            List<String> lotCpvs) {
        List<SuggestedGppCriterion> suggested = new java.util.ArrayList<>();
        for (GppCriterion criterion : criteria) {
            // compute matching CPVs between the lot and the criterion
            List<String> matchingCpvs = Utils.matchingCpvs(lotCpvs, criterion.getRelevantCpvCodes());

            SuggestedGppCriterion s = new SuggestedGppCriterion(
                    criterion.getGppDocument(),
                    criterion.getCategory(),
                    criterion.getCriterionType(),
                    criterion.getAmbitionLevel(),
                    criterion.getId(),
                    criterion.getName(),
                    criterion.getRelevantCpvCodes(),
                    matchingCpvs,
                    lotId);
            suggested.add(s);
        }
        return suggested;
    }

    public List<SuggestedGppPatch> suggestGppPatches(List<SuggestedGppCriterion> suggestedCriteria) {

        // private final GppPatchesLoader patchesLoader = new GppPatchesLoader();

        // remember to include the global patches (e.g. document, changes section)
        // will probs need to convert he SuggestedGppCriterion to GppCriterion

        // we first need to convert the suggested criteria to GppCriteria to get the
        // internal details

        // ??++ AQUI
        return List.of(); // Placeholder return
    }

    private List<GppCriterion> convertToGppCriteria(List<SuggestedGppCriterion> suggestedCriteria) {
        // List<GppCriterion> criteria = new java.util.ArrayList<>();
        // for (SuggestedGppCriterion s : suggestedCriteria) {
        // GppCriterion c = new GppCriterion(
        // s.getGppDocument(),
        // s.getCategory(),
        // s.getCriterionType(),
        // s.getAmbitionLevel(),
        // s.getId(),
        // s.getName(),
        // s.getRelevantCpvCodes());
        // criteria.add(c);
        // }
        // return criteria;
        return List.of(); // Placeholder return
    }

}
