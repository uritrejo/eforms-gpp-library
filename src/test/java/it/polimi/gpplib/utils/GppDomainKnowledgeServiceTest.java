package it.polimi.gpplib.utils;

import org.junit.Test;

import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class GppDomainKnowledgeServiceTest {

    @Test
    public void testGppDomainKnowledgeServiceInitialization_invalidGppDocs() {
        String gppDocsPath = "invalid_path/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        try {
            new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid GPP documents file path: invalid_path/test_gpp_criteria_docs.json", ex.getMessage());
        }
    }

    @Test
    public void testGppDomainKnowledgeServiceInitialization_invalidGppCriteria() {
        String gppDocsPath = "domain_knowledge/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "invalid_path/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        try {
            new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid GPP criteria file path: invalid_path/test_gpp_criteria.json", ex.getMessage());
        }
    }

    @Test
    public void testGppDomainKnowledgeServiceInitialization_invalidGppPatches() {
        String gppDocsPath = "domain_knowledge/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "invalid_path/test_gpp_patches_data.json";
        try {
            new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid GPP patches file path: invalid_path/test_gpp_patches_data.json", ex.getMessage());
        }
    }

    @Test
    public void testGppDomainKnowledgeServiceInitialization_success() {
        // Test initialization with valid paths
        String gppDocsPath = "domain_knowledge/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        GppDomainKnowledgeService service = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        assertNotNull(service);
    }

    @Test
    public void testGetRelevantGppDocuments() {
        String gppDocsPath = "domain_knowledge/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        GppDomainKnowledgeService service = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        List<String> cpvs = List.of("11111111");
        List<GppDocument> relevantDocs = service.getRelevantGppDocuments(cpvs);
        assertTrue(relevantDocs.size() == 1);
        assertEquals("Doc1", relevantDocs.get(0).getName());
    }

    @Test
    public void testGetRelevantGppCriteria() {
        String gppDocsPath = "domain_knowledge/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        GppDomainKnowledgeService service = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        List<String> cpvs = List.of("40000000");
        String ambitionLevel = "comprehensive";
        List<GppCriterion> relevantCriteria = service.getRelevantGppCriteria(cpvs, ambitionLevel);
        assertTrue(relevantCriteria.size() == 1);
        assertEquals("ID2", relevantCriteria.get(0).getId());
    }

    @Test
    public void testConvertToSuggestedGppCriteria() {
        String gppDocsPath = "domain_knowledge/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        GppDomainKnowledgeService service = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);

        List<GppCriterion> gppCriteria = service.getRelevantGppCriteria(List.of("40000000"), "comprehensive");
        assertTrue(gppCriteria.size() == 1);
        assertEquals("ID2", gppCriteria.get(0).getId());

        List<SuggestedGppCriterion> suggestedCriteria = service.convertToSuggestedGppCriteria(gppCriteria, "LOT-0001",
                List.of("40000000"));

        assertTrue(suggestedCriteria.size() == 1);
        SuggestedGppCriterion suggested = suggestedCriteria.get(0);
        assertEquals("DocB", suggested.getGppDocument());
        assertEquals("catB", suggested.getCategory());
        assertEquals("typeB", suggested.getCriterionType());
        assertEquals("comprehensive", suggested.getAmbitionLevel());
        assertEquals("ID2", suggested.getId());
        assertEquals("NameB", suggested.getName());
        assertEquals(List.of("30000000", "40000000"), suggested.getRelevantCpvCodes());
        assertEquals(List.of("40000000"), suggested.getMatchingCpvCodes());
        assertEquals("LOT-0001", suggested.getLotId());
    }

    // the detailed tests are in the GppPatchSuggesterTest class
    @Test
    public void testSuggestGppPatches() {
        String gppDocsPath = "domain_knowledge/test_gpp_criteria_docs.json";
        String gppCriteriaPath = "domain_knowledge/test_gpp_criteria.json";
        String gppPatchesPath = "domain_knowledge/test_gpp_patches_data.json";
        GppDomainKnowledgeService service = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        String noticeXmlString = XmlUtils.getAsXmlString("test_notice_minimal.xml");
        Notice notice = new Notice(noticeXmlString);
        List<SuggestedGppCriterion> suggestedCriteria = new ArrayList<>();
        List<SuggestedGppPatch> suggestedPatches = service.suggestGppPatches(notice, suggestedCriteria);
        assertTrue(suggestedPatches.isEmpty());
    }

}
