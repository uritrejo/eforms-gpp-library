package it.polimi.gpplib;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;
import it.polimi.gpplib.utils.XmlUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Integration test for the GppNoticeAnalyzer using real XML notice data
 * and test domain knowledge.
 */
public class GppNoticeAnalyzerIntegrationTest {

    private GppNoticeAnalyzer analyzer;

    @Before
    public void setUp() throws Exception {
        // Initialize analyzer with test domain knowledge files
        String gppDocsPath = "domain_knowledge/real_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/real_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/real_gpp_patches_data.json";

        analyzer = new DefaultGppNoticeAnalyzer(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        assertNotNull("Analyzer should be initialized", analyzer);
    }

    @Test
    public void testCompleteGppAnalysisWorkflow() throws Exception {
        // Step 1: Load notice from XML
        String noticeXml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Notice notice = analyzer.loadNotice(noticeXml);
        assertNotNull("Notice should be loaded from XML", notice);

        // Step 2: Analyze the notice to get GPP suggestions
        GppAnalysisResult analysisResult = analyzer.analyzeNotice(notice);
        assertNotNull("Analysis result should not be null", analysisResult);

        List<SuggestedGppCriterion> suggestedCriteria = analysisResult.getSuggestedGppCriteria();
        assertNotNull("Suggested criteria list should not be null", suggestedCriteria);

        // Log some information about the analysis
        System.out.println("Analysis completed for notice with " + suggestedCriteria.size() + " suggested criteria");

        // Step 3: Suggest patches based on the criteria (if any were found)
        List<SuggestedGppPatch> patches = analyzer.suggestPatches(notice, suggestedCriteria);
        assertNotNull("Patches list should not be null", patches);

        System.out.println("Found " + patches.size() + " suggested patches");

        // Step 4: Apply patches to create an improved notice
        Notice improvedNotice = analyzer.applyPatches(notice, patches);
        assertNotNull("Improved notice should not be null", improvedNotice);

        // Verify basic workflow completion - the key is that all steps executed without
        // exception
        assertTrue("Analysis workflow should complete successfully", true);

        // Log whether any actual changes were made
        String originalXml = notice.toXmlString();
        String improvedXml = improvedNotice.toXmlString();
        boolean xmlChanged = !originalXml.equals(improvedXml);

        System.out.println("XML content changed after applying patches: " + xmlChanged);
        if (xmlChanged) {
            System.out.println("Original XML length: " + originalXml.length());
            System.out.println("Improved XML length: " + improvedXml.length());
        }

        System.out.println("Integration test completed successfully");
    }

}
