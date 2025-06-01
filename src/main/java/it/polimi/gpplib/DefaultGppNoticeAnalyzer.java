package it.polimi.gpplib;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.SuggestedGppPatch;
import it.polimi.gpplib.utils.GppDomainKnowledgeService;
import it.polimi.gpplib.utils.Utils;

import java.util.List;

/**
 * Default implementation of the GppNoticeAnalyzer interface.
 */
public class DefaultGppNoticeAnalyzer implements GppNoticeAnalyzer {

    private final GppDomainKnowledgeService domainKnowledge;

    // TODO: eventually, this should come from a config
    private String ambitionLevel = GppCriterion.AMBITION_LEVEL_CORE;

    // TODO: eventually you'll need to take in config params
    public DefaultGppNoticeAnalyzer() {
        domainKnowledge = new GppDomainKnowledgeService();
    }

    @Override
    public Notice loadNotice(String xmlString) {
        Notice notice = new Notice(xmlString);

        // TODO: add notice validation (e.g. noticeType, version, language, etc.)

        return notice;
    }

    @Override
    public GppAnalysisResult analyzeNotice(Notice notice) {
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
        return null;
    }

    // temporary for testing purposes
    public static void main(String[] args) {
        DefaultGppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer();
        String noticePath = "notices_furniture/00155175_2025.xml";
        String noticeXml = Utils.loadXmlString(noticePath);
        Notice notice = analyzer.loadNotice(noticeXml);
        GppAnalysisResult result = analyzer.analyzeNotice(notice);
        System.out.println("Notice: " + notice);
        System.out.println("Analysis Result: " + result);

        List<SuggestedGppPatch> patches = analyzer.suggestPatches(notice, result.getSuggestedGppCriteria());
        System.out.println("Suggested Patches: " + patches);
    }
}