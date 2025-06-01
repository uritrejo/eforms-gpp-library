package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.List;

public class GppDomainKnowledgeService {
    public GppDomainKnowledgeService() {
        // Load documents, criteria, patches from resources
    }

    public List<GppDocument> getRelevantGppDocuments(List<String> cpvs) {
        // TODO: eventually, the relevant documents should only come from the relevant
        // GPP criteria (looking at the document names)
        return List.of(); // Placeholder return
    }

    public List<GppCriterion> getRelevantGppCriteria(List<String> cpvs) {
        // ...
        return List.of(); // Placeholder return
    }

    // TODO: probably change the name (like convertToSuggestedGppCriteria or smt
    // descriptive)
    public List<SuggestedGppCriterion> suggestGppCriteria(List<GppCriterion> criteria) {
        // ...
        return List.of(); // Placeholder return
    }

    public List<SuggestedGppPatch> suggestGppPatches(List<SuggestedGppCriterion> criteria) {
        // remember to include the global patches (e.g. document, changes section)
        // will probs need to convert he SuggestedGppCriterion to GppCriterion
        return List.of(); // Placeholder return
    }

}
