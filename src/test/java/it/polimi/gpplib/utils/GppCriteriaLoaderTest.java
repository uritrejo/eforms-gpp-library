package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppCriterion;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GppCriteriaLoaderTest {

    @Test
    public void testLoadGppCriteria_withTestFile() throws Exception {
        String testFilePath = "domain_knowledge/test_gpp_criteria.json";
        GppCriteriaLoader loader = new GppCriteriaLoader(testFilePath);
        List<GppCriterion> criteria = loader.loadGppCriteria();

        assertNotNull(criteria);
        assertEquals(3, criteria.size());

        GppCriterion crit1 = criteria.get(0);
        assertEquals("DocA", crit1.getGppDocument());
        assertEquals("srcA", crit1.getGppSource());
        assertEquals("catA", crit1.getCategory());
        assertEquals("typeA", crit1.getCriterionType());
        assertEquals("core", crit1.getAmbitionLevel());
        assertEquals("ID1", crit1.getId());
        assertEquals("NameA", crit1.getName());
        assertEquals(List.of("10000000", "20000000"), crit1.getRelevantCpvCodes());
        assertEquals("impactA", crit1.getEnvironmentalImpactType());
        assertEquals("descA", crit1.getDescription());
        assertNull(crit1.getSelectionCriterionType());

        GppCriterion crit2 = criteria.get(1);
        assertEquals("DocB", crit2.getGppDocument());
        assertEquals("srcB", crit2.getGppSource());
        assertEquals("catB", crit2.getCategory());
        assertEquals("typeB", crit2.getCriterionType());
        assertEquals("comprehensive", crit2.getAmbitionLevel());
        assertEquals("ID2", crit2.getId());
        assertEquals("NameB", crit2.getName());
        assertEquals(List.of("30000000", "40000000"), crit2.getRelevantCpvCodes());
        assertEquals("impactB", crit2.getEnvironmentalImpactType());
        assertEquals("descB", crit2.getDescription());
        assertNull(crit2.getSelectionCriterionType());

        GppCriterion crit3 = criteria.get(2);
        assertEquals("DocC", crit3.getGppDocument());
        assertEquals("srcC", crit3.getGppSource());
        assertEquals("catC", crit3.getCategory());
        assertEquals("typeC", crit3.getCriterionType());
        assertEquals("core", crit3.getAmbitionLevel());
        assertEquals("ID3", crit3.getId());
        assertEquals("NameC", crit3.getName());
        assertEquals(List.of("50000000"), crit3.getRelevantCpvCodes());
        assertEquals("impactC", crit3.getEnvironmentalImpactType());
        assertEquals("descC", crit3.getDescription());
        assertEquals("selTypeC", crit3.getSelectionCriterionType());
    }
}
