package it.polimi.gpplib;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.SuggestedGppPatch;
import it.polimi.gpplib.utils.GppDomainKnowledgeService;

import java.util.List;

/**
 * Default implementation of the GppNoticeAnalyzer interface.
 */
public class DefaultGppNoticeAnalyzer implements GppNoticeAnalyzer {

    private final GppDomainKnowledgeService domainKnowledge;

    // TODO: eventually you'll need to take in config params
    public DefaultGppNoticeAnalyzer() {
        domainKnowledge = new GppDomainKnowledgeService();
    }

    /*
     * PLAN:
     * - load the notice from XML
     * - follow the python notebook to get the relevant fields
     * - requires the domain knowledge + paths to the lot
     * - requires implementing the notice constructor from xml
     * - and properly defining how this could work
     */

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
        List<SuggestedGppPatch> allSuggestedPatches = new java.util.ArrayList<>();

        List<String> projectCpvs = notice.getAllProcurementProjectCpvs();
        List<String> lotIds = notice.getLotIds();
        for (String lotId : lotIds) {
            List<String> lotCpvs = notice.getAllLotCpvs(lotId);
            if (lotCpvs.isEmpty()) {
                lotCpvs = projectCpvs;
            }

            List<GppDocument> relevantDocuments = domainKnowledge.getRelevantGppDocuments(lotCpvs);
            List<GppCriterion> relevantCriteria = domainKnowledge.getRelevantGppCriteria(lotCpvs);
            List<SuggestedGppCriterion> suggestedCriteria = domainKnowledge.suggestGppCriteria(relevantCriteria);
            List<SuggestedGppPatch> suggestedPatches = domainKnowledge.suggestGppPatches(relevantCriteria);

            allRelevantDocuments.addAll(relevantDocuments);
            allSuggestedCriteria.addAll(suggestedCriteria);
            allSuggestedPatches.addAll(suggestedPatches);
        }

        return new GppAnalysisResult(new java.util.ArrayList<>(allRelevantDocuments), allSuggestedCriteria,
                allSuggestedPatches);
    }

    @Override
    public Notice applyPatches(List<SuggestedGppPatch> patches, Notice notice) {
        // TODO: Implement patch application logic
        return null;
    }

    // temporary for testing purposes
    public static void main(String[] args) {
        DefaultGppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer();
        String sampleXml = "<notice></notice>"; // Replace with actual test XML
        Notice notice = analyzer.loadNotice(sampleXml);
        GppAnalysisResult result = analyzer.analyzeNotice(notice);
        System.out.println("Notice: " + notice);
        System.out.println("Analysis Result: " + result);
    }
}