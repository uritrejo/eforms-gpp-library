package it.polimi.gpplib;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.SuggestedGppPatch;
import it.polimi.gpplib.utils.GppDomainKnowledgeService;
import it.polimi.gpplib.utils.GppPatchApplier;
import it.polimi.gpplib.utils.XmlUtils;
import it.polimi.gpplib.utils.XmlUtils.XmlUtilsException;

import java.util.List;

/**
 * Default implementation of the GppNoticeAnalyzer interface.
 */
public class DefaultGppNoticeAnalyzer implements GppNoticeAnalyzer {

    private final GppDomainKnowledgeService domainKnowledge;

    private final GppPatchApplier patchApplier;

    // TODO: eventually, this should come from a config
    private String ambitionLevel = GppCriterion.AMBITION_LEVEL_CORE;

    // TODO: eventually you'll need to take in config params
    public DefaultGppNoticeAnalyzer(String gppDocsPath, String gppCriteriaPath, String gppPatchesPath) {
        try {
            domainKnowledge = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        } catch (IllegalArgumentException e) {
            throw new GppBadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error loading domain knowledge", e);
        }
        patchApplier = new GppPatchApplier();
    }

    @Override
    public Notice loadNotice(String xmlString) {
        Notice notice;
        try {
            notice = new Notice(xmlString);
        } catch (XmlUtilsException e) {
            throw new GppBadRequestException("Invalid notice xml string: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new GppBadRequestException("Unexpecter error loading notice: " + e.getMessage(), e);
        }

        // TODO: add some notice validation (e.g. noticeType, version, language, etc.)

        return notice;
    }

    @Override
    public GppAnalysisResult analyzeNotice(Notice notice) {
        try {
            // TODO: verify that the documents can't be duplicated
            java.util.Set<GppDocument> allRelevantDocuments = new java.util.HashSet<>(); // to avoid duplicates
            List<SuggestedGppCriterion> allSuggestedCriteria = new java.util.ArrayList<>();

            List<String> projectCpvs = notice.getAllProcurementProjectCpvs();
            List<String> lotIds = notice.getLotIds();
            for (String lotId : lotIds) {
                List<String> lotCpvs = notice.getAllLotCpvs(lotId);
                if (lotCpvs.isEmpty()) {
                    lotCpvs = projectCpvs;
                }

                List<GppDocument> relevantDocuments = domainKnowledge.getRelevantGppDocuments(lotCpvs);
                List<GppCriterion> relevantCriteria = domainKnowledge.getRelevantGppCriteria(lotCpvs, ambitionLevel);
                List<SuggestedGppCriterion> suggestedCriteria = domainKnowledge
                        .convertToSuggestedGppCriteria(relevantCriteria, lotId, lotCpvs);

                allRelevantDocuments.addAll(relevantDocuments);
                allSuggestedCriteria.addAll(suggestedCriteria);
            }
            return new GppAnalysisResult(new java.util.ArrayList<>(allRelevantDocuments), allSuggestedCriteria);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error during notice analysis: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SuggestedGppPatch> suggestPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria) {
        // TODO: validate suggestedCriteria & return a GppBadRequestException if invalid
        try {
            return domainKnowledge.suggestGppPatches(notice, suggestedCriteria);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error during patch suggestion: " + e.getMessage(), e);
        }
    }

    @Override
    public Notice applyPatches(Notice notice, List<SuggestedGppPatch> patches) {
        try {
            for (SuggestedGppPatch patch : patches) {
                if (patch == null) {
                    continue;
                }
                notice = patchApplier.applyPatch(notice, patch);
            }
            return notice;
        } catch (IllegalArgumentException e) {
            throw new GppBadRequestException("Invalid patch: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error applying patches: " + e.getMessage(), e);
        }
    }

    // temporary for testing purposes
    public static void main(String[] args) {
        try {
            DefaultGppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer(Constants.DOMAIN_KNOWLEDGE_GPP_DOCS_PATH,
                    Constants.DOMAIN_KNOWLEDGE_GPP_CRITERIA_PATH, Constants.DOMAIN_KNOWLEDGE_GPP_PATCHES_PATH);
            // String noticePath = "notices_furniture/00155175_2025.xml";
            String noticePath = "notices_furniture/dummy.xml";
            String noticeXml = XmlUtils.getAsXmlString(noticePath);
            Notice notice = analyzer.loadNotice(noticeXml);
            GppAnalysisResult result = analyzer.analyzeNotice(notice);
            System.out.println("Notice: " + notice);
            System.out.println("Analysis Result: " + result);

            List<SuggestedGppPatch> patches = analyzer.suggestPatches(notice, result.getSuggestedGppCriteria());
            System.out.println("Suggested Patches: " + patches);

            Notice patchedNotice = analyzer.applyPatches(notice, patches);
            noticeXml = patchedNotice.toXmlString();
            System.out.println("Patched Notice: " + noticeXml);
        } catch (Exception e) {
            System.err.println("An error occurred during analysis:");
            e.printStackTrace();
        }
    }
}