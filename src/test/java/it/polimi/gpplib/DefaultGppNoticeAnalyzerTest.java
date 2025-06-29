package it.polimi.gpplib;

import org.junit.Test;
import org.w3c.dom.Node;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;
import it.polimi.gpplib.utils.XmlUtils;

import static org.junit.Assert.*;

import java.util.List;

public class DefaultGppNoticeAnalyzerTest {

    DefaultGppNoticeAnalyzer analyzer;

    @org.junit.Before
    public void setUp() {
        String gppDocsPath = "domain_knowledge/real_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/real_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/real_gpp_patches_data.json";
        analyzer = new DefaultGppNoticeAnalyzer(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        assertNotNull(analyzer);
    }

    @Test
    public void testInit_errorLoadingDomainKnowledge() {
        String gppDocsPath = "invalid_path/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        try {
            new DefaultGppNoticeAnalyzer(gppDocsPath, gppCriteriaPath, gppPatchesPath);
            fail("Expected GppBadRequestException");
        } catch (GppBadRequestException ex) {
            assertEquals("Invalid GPP documents file path: invalid_path/test_gpp_criteria_docs.json", ex.getMessage());
        }
    }

    @Test
    public void testInit_validWithParams() {
        String gppDocsPath = "domain_knowledge/real_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/real_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/real_gpp_patches_data.json";
        DefaultGppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        assertNotNull(analyzer);
    }

    @Test
    public void testInit_validNoParams() {
        DefaultGppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer();
        assertNotNull(analyzer);
    }

    @Test
    public void testLoadNotice_invalidNotice() {
        try {
            analyzer.loadNotice("<invalid...");
            fail("Expected GppBadRequestException");
        } catch (GppBadRequestException ex) {
            assertEquals("Invalid notice xml string: Failed to parse XML string", ex.getMessage());
        }
    }

    @Test
    public void testLoadNotice_validNotice() {
        String noticeXml = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
        Notice notice = analyzer.loadNotice(noticeXml);
        assertNotNull(notice);
        assertEquals("ContractNotice", notice.getDoc().getDocumentElement().getLocalName());
    }

    @Test
    public void testAnalyzeNotice_invalidNotice() {
        try {
            analyzer.analyzeNotice(null);
            fail("Expected GppInternalErrorException");
        } catch (GppBadRequestException ex) {
            assertEquals(
                    "Notice must not be null",
                    ex.getMessage());
        }
    }

    @Test
    public void testAnalyzeNotice() {
        String noticeXml = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
        Notice notice = analyzer.loadNotice(noticeXml);
        GppAnalysisResult result = analyzer.analyzeNotice(notice);
        assertEquals(result.getRelevantGppDocuments().get(0).getName(), "EU GPP Criteria for Furniture");
        // Assert all suggested criteria have the expected properties
        for (var suggestedGppCriterion : result.getSuggestedGppCriteria()) {
            assertEquals("core", suggestedGppCriterion.getAmbitionLevel().toLowerCase());
            assertEquals("EU GPP Criteria for Furniture", suggestedGppCriterion.getGppDocument());
            assertEquals("LOT-0002", suggestedGppCriterion.getLotId());
            assertArrayEquals(new String[] { "39110000" }, suggestedGppCriterion.getMatchingCpvCodes().toArray());
        }
    }

    @Test
    public void testSuggestPatches_invalidCriteria() {
        try {
            analyzer.suggestPatches(null, null);
            fail("Expected GppInternalErrorException");
        } catch (GppBadRequestException ex) {
            assertEquals(
                    "Notice and suggested criteria must not be null",
                    ex.getMessage());
        }
    }

    @Test
    public void testSuggestPatches() {
        String noticeXml = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
        Notice notice = analyzer.loadNotice(noticeXml);
        List<SuggestedGppCriterion> suggestedCriteria = List.of(
                new SuggestedGppCriterion("EU GPP Criteria for Furniture",
                        "Procurement of furniture refurbishment services", "Award criteria", "core", "AC1",
                        "Low chemical residue upholstery coverings", List.of("39110000"), List.of("39110000"),
                        "LOT-0002"));
        List<SuggestedGppPatch> suggestedPatches = analyzer.suggestPatches(notice, suggestedCriteria);
        assertPatchExists(suggestedPatches, "Green Public Procurement Criteria - eu");
        assertPatchExists(suggestedPatches, "Green Procurement - other");
        assertPatchExists(suggestedPatches, "Strategic Procurement: Reduction of environmental impacts");
        assertPatchExists(suggestedPatches, "Tendering Terms");
        assertPatchExists(suggestedPatches, "Awarding Terms");
        assertPatchExists(suggestedPatches, "Award Criteria");
        assertPatchExists(suggestedPatches, "Award criteria --- AC1: Extended warranty periods");
    }

    @Test
    public void testApplyPatches_invalidPatch() {
        try {
            String noticeXml = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
            Notice notice = analyzer.loadNotice(noticeXml);
            List<SuggestedGppPatch> patches = List.of(new SuggestedGppPatch("invalidPatch", null, null,
                    Constants.PATH_PROCUREMENT_PROJECT, "<value>test</value>", "invalidOp", "Test patch", "LOT-0001"));
            analyzer.applyPatches(notice, patches);
            fail("Expected GppBadRequestException");
        } catch (GppBadRequestException ex) {
            assertEquals(
                    "Invalid patch: Invalid patch operation: invalidOp",
                    ex.getMessage());
        }
    }

    @Test
    public void testApplyPatches() {
        String noticeXml = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
        Notice notice = analyzer.loadNotice(noticeXml);
        List<SuggestedGppPatch> patches = List.of(
                new SuggestedGppPatch("add potato", null, null,
                        Constants.PATH_PROCUREMENT_PROJECT, "<dummyValue>potato</dummyValue>", "create", "Adds potato",
                        "LOT-0001"),
                new SuggestedGppPatch("add broccoli", null, null,
                        Constants.PATH_PROCUREMENT_PROJECT, "<dummyValue><internal>broccoli</internal></dummyValue>",
                        "create", "Adds broccoli",
                        "LOT-0002"));
        Notice patchedNotice = analyzer.applyPatches(notice, patches);
        Node lot1 = patchedNotice.getLotNode("LOT-0001");
        String insertedValue = XmlUtils.getNodeValueAtPath(lot1, Constants.PATH_PROCUREMENT_PROJECT + "/dummyValue");
        assertEquals("potato", insertedValue);
        Node lot2 = patchedNotice.getLotNode("LOT-0002");
        insertedValue = XmlUtils.getNodeValueAtPath(lot2,
                Constants.PATH_PROCUREMENT_PROJECT + "/dummyValue/internal");
        assertEquals("broccoli", insertedValue);
    }

    private void assertPatchExists(List<SuggestedGppPatch> suggestedPatches, String patchName) {
        assertTrue(suggestedPatches.stream().anyMatch(patch -> patch.getName().equals(patchName)));
    }
}
