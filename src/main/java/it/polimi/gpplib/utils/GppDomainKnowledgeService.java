package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GppDomainKnowledgeService {

    private static final Logger logger = LoggerFactory.getLogger(GppDomainKnowledgeService.class);

    private List<GppDocument> gppDocs = new java.util.ArrayList<>();
    private List<GppCriterion> gppCriteria = new java.util.ArrayList<>();
    private List<GppPatch> gppPatches = new java.util.ArrayList<>();

    private final GppPatchSuggester patchSuggester;

    public GppDomainKnowledgeService(String gppDocsPath, String gppCriteriaPath, String gppPatchesPath) {
        logger.info("Loading domain knowledge from paths: docs={}, criteria={}, patches={}",
                gppDocsPath, gppCriteriaPath, gppPatchesPath);

        try {
            GppDocumentsLoader docsLoader = new GppDocumentsLoader(gppDocsPath);
            gppDocs = docsLoader.loadGppDocuments();
            logger.info("Loaded {} GPP documents", gppDocs.size());
        } catch (Exception e) {
            logger.error("Failed to load GPP documents from path: {}", gppDocsPath, e);
            throw new IllegalArgumentException("Invalid GPP documents file path: " + gppDocsPath, e);
        }

        try {
            GppCriteriaLoader criteriaLoader = new GppCriteriaLoader(gppCriteriaPath);
            gppCriteria = criteriaLoader.loadGppCriteria();
            logger.info("Loaded {} GPP criteria", gppCriteria.size());
        } catch (Exception e) {
            logger.error("Failed to load GPP criteria from path: {}", gppCriteriaPath, e);
            throw new IllegalArgumentException("Invalid GPP criteria file path: " + gppCriteriaPath, e);
        }

        try {
            GppPatchesLoader patchesLoader = new GppPatchesLoader(gppPatchesPath);
            gppPatches = patchesLoader.loadGppPatches();
            logger.info("Loaded {} GPP patches", gppPatches.size());
        } catch (Exception e) {
            logger.error("Failed to load GPP patches from path: {}", gppPatchesPath, e);
            throw new IllegalArgumentException("Invalid GPP patches file path: " + gppPatchesPath, e);
        }

        patchSuggester = new GppPatchSuggester(gppCriteria, gppPatches);
        logger.debug("GppDomainKnowledgeService initialization completed successfully");
    }

    // TODO: eventually, the relevant documents should only come from the relevant
    // GPP criteria (looking at the document names)
    public List<GppDocument> getRelevantGppDocuments(List<String> cpvs) {
        logger.debug("Finding relevant GPP documents for {} CPV codes", cpvs.size());
        List<GppDocument> relevantGppDocs = new java.util.ArrayList<>();
        for (GppDocument doc : gppDocs) {
            if (doc.isApplicable(cpvs)) {
                relevantGppDocs.add(doc);
            }
        }
        logger.debug("Found {} relevant GPP documents", relevantGppDocs.size());
        return relevantGppDocs;
    }

    public List<GppCriterion> getRelevantGppCriteria(List<String> cpvs, String ambitionLevel) {
        logger.debug("Finding relevant GPP criteria for {} CPV codes with ambition level: {}", cpvs.size(),
                ambitionLevel);
        List<GppCriterion> relevantGppCriteria = new java.util.ArrayList<>();
        for (GppCriterion criterion : gppCriteria) {
            if (criterion.isApplicable(cpvs, ambitionLevel)) {
                relevantGppCriteria.add(criterion);
            }
        }
        logger.debug("Found {} relevant GPP criteria", relevantGppCriteria.size());
        return relevantGppCriteria;
    }

    /**
     * Converts a list of GppCriterion to SuggestedGppCriterion for a given lot.
     * The matching CPVs are computed for each criterion.
     */
    public List<SuggestedGppCriterion> convertToSuggestedGppCriteria(List<GppCriterion> criteria, String lotId,
            List<String> lotCpvs) {
        logger.debug("Converting {} criteria to suggested criteria for lot: {}", criteria.size(), lotId);
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
        logger.debug("Created {} suggested criteria for lot: {}", suggested.size(), lotId);
        return suggested;
    }

    public List<SuggestedGppPatch> suggestGppPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria) {
        logger.debug("Suggesting GPP patches for {} criteria", suggestedCriteria.size());
        List<SuggestedGppPatch> patches = patchSuggester.suggestGppPatches(notice, suggestedCriteria);
        logger.debug("Generated {} patch suggestions", patches.size());
        return patches;
    }
}
