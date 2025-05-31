package it.polimi.gpplib;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.SuggestedGppPatch;
import java.util.List;

/**
 * Default implementation of the GppNoticeAnalyzer interface.
 */
public class DefaultGppNoticeAnalyzer implements GppNoticeAnalyzer {

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
        // TODO: Implement notice analysis logic
        return null;
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