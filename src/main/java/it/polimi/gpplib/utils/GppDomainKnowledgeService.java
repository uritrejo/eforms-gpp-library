package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.List;

public class GppDomainKnowledgeService {

    private List<GppDocument> gppDocs = new java.util.ArrayList<>();
    private List<GppCriterion> gppCriteria = new java.util.ArrayList<>();
    private List<GppPatch> gppPatches = new java.util.ArrayList<>();

    private final GppPatchSuggester patchSuggester;

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

        patchSuggester = new GppPatchSuggester(gppCriteria, gppPatches);
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
            SuggestedGppCriterion s = new SuggestedGppCriterion(
                    criterion.getGppDocument(),
                    criterion.getCategory(),
                    criterion.getCriterionType(),
                    criterion.getAmbitionLevel(),
                    criterion.getId(),
                    criterion.getName(),
                    criterion.getRelevantCpvCodes(),
                    Utils.matchingCpvs(lotCpvs, criterion.getRelevantCpvCodes()),
                    lotId);
            suggested.add(s);
        }
        return suggested;
    }

    public List<SuggestedGppPatch> suggestGppPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria) {
        return patchSuggester.suggestGppPatches(notice, suggestedCriteria);
    }
}
