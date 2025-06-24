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
        domainKnowledge = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        patchApplier = new GppPatchApplier();
    }

    @Override
    public Notice loadNotice(String xmlString) {
        Notice notice = new Notice(xmlString);

        // TODO: add notice validation (e.g. noticeType, version, language, etc.)

        return notice;
    }

    @Override
    public GppAnalysisResult analyzeNotice(Notice notice) {
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
    }

    @Override
    public List<SuggestedGppPatch> suggestPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria) {
        return domainKnowledge.suggestGppPatches(notice, suggestedCriteria);
    }

    @Override
    public Notice applyPatches(Notice notice, List<SuggestedGppPatch> patches) {
        for (SuggestedGppPatch patch : patches) {
            if (patch == null) {
                continue;
            }
            notice = patchApplier.applyPatch(notice, patch);
        }
        return notice;
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