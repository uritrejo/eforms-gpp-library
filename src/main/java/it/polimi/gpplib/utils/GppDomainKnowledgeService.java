package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.List;

public class GppDomainKnowledgeService {
    private final GppDocumentsLoader docsLoader = new GppDocumentsLoader();
    private final GppCriteriaLoader criteriaLoader = new GppCriteriaLoader();
    private final GppPatchesLoader patchesLoader = new GppPatchesLoader();

    public GppDomainKnowledgeService() {
    }

    // TODO: eventually, the relevant documents should only come from the relevant
    // GPP criteria (looking at the document names)
    public List<GppDocument> getRelevantGppDocuments(List<String> cpvs) {
        List<GppDocument> relevantGppDocs = new java.util.ArrayList<>();
        try {
            List<GppDocument> gppDocs = docsLoader.loadGppDocuments();
            for (GppDocument doc : gppDocs) {
                if (doc.isApplicable(cpvs)) {
                    relevantGppDocs.add(doc);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load GPP documents: " + e.getMessage());
            e.printStackTrace();
        }

        return relevantGppDocs;
    }

    public List<GppCriterion> getRelevantGppCriteria(List<String> cpvs, String ambitionLevel) {
        List<GppCriterion> relevantGppCriteria = new java.util.ArrayList<>();
        try {
            List<GppCriterion> gppCriteria = criteriaLoader.loadGppCriteria();
            for (GppCriterion criterion : gppCriteria) {
                if (criterion.isApplicable(cpvs, ambitionLevel)) {
                    relevantGppCriteria.add(criterion);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load GPP criteria: " + e.getMessage());
            e.printStackTrace();
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

    public List<SuggestedGppPatch> suggestGppPatches(List<SuggestedGppCriterion> criteria) {
        // remember to include the global patches (e.g. document, changes section)
        // will probs need to convert he SuggestedGppCriterion to GppCriterion
        return List.of(); // Placeholder return
    }

}
