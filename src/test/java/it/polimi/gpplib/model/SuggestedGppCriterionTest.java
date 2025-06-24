package it.polimi.gpplib.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SuggestedGppCriterionTest {

    @Test
    public void testGettersAndSetters() {
        SuggestedGppCriterion crit = new SuggestedGppCriterion();
        crit.setGppDocument("doc");
        crit.setCategory("cat");
        crit.setCriterionType("type");
        crit.setAmbitionLevel("core");
        crit.setId("ID1");
        crit.setName("name");
        crit.setRelevantCpvCodes(Arrays.asList("12345678", "87654321"));
        crit.setMatchingCpvCodes(Collections.singletonList("12345678"));
        crit.setLotId("LOT-1");

        assertEquals("doc", crit.getGppDocument());
        assertEquals("cat", crit.getCategory());
        assertEquals("type", crit.getCriterionType());
        assertEquals("core", crit.getAmbitionLevel());
        assertEquals("ID1", crit.getId());
        assertEquals("name", crit.getName());
        assertEquals(Arrays.asList("12345678", "87654321"), crit.getRelevantCpvCodes());
        assertEquals(Collections.singletonList("12345678"), crit.getMatchingCpvCodes());
        assertEquals("LOT-1", crit.getLotId());
    }

    @Test
    public void testAllArgsConstructor() {
        List<String> relevantCpvs = Arrays.asList("12345678", "87654321");
        List<String> matchingCpvs = Collections.singletonList("12345678");
        SuggestedGppCriterion crit = new SuggestedGppCriterion(
                "doc", "cat", "type", "core", "ID1", "name", relevantCpvs, matchingCpvs, "LOT-1"
        );

        assertEquals("doc", crit.getGppDocument());
        assertEquals("cat", crit.getCategory());
        assertEquals("type", crit.getCriterionType());
        assertEquals("core", crit.getAmbitionLevel());
        assertEquals("ID1", crit.getId());
        assertEquals("name", crit.getName());
        assertEquals(relevantCpvs, crit.getRelevantCpvCodes());
        assertEquals(matchingCpvs, crit.getMatchingCpvCodes());
        assertEquals("LOT-1", crit.getLotId());
    }

    @Test
    public void testToString() {
        List<String> relevantCpvs = Arrays.asList("12345678", "87654321");
        List<String> matchingCpvs = Collections.singletonList("12345678");
        SuggestedGppCriterion crit = new SuggestedGppCriterion(
                "doc", "cat", "type", "core", "ID1", "name", relevantCpvs, matchingCpvs, "LOT-1"
        );
        String str = crit.toString();
        assertTrue(str.contains("SuggestedGppCriterion{"));
        assertTrue(str.contains("doc"));
        assertTrue(str.contains("cat"));
        assertTrue(str.contains("type"));
        assertTrue(str.contains("core"));
        assertTrue(str.contains("ID1"));
        assertTrue(str.contains("name"));
        assertTrue(str.contains("12345678"));
        assertTrue(str.contains("LOT-1"));
    }
}
