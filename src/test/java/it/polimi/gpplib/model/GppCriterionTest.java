package it.polimi.gpplib.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GppCriterionTest {

    @Test
    public void testGettersAndSetters() {
        GppCriterion criterion = new GppCriterion();
        criterion.setGppDocument("doc");
        criterion.setGppSource("eu");
        criterion.setCategory("cat");
        criterion.setCriterionType("type");
        criterion.setAmbitionLevel("core");
        criterion.setId("ID1");
        criterion.setName("name");
        criterion.setRelevantCpvCodes(Arrays.asList("12345678", "87654321"));
        criterion.setEnvironmentalImpactType("impact");
        criterion.setDescription("desc");
        criterion.setSelectionCriterionType("selType");

        assertEquals("doc", criterion.getGppDocument());
        assertEquals("eu", criterion.getGppSource());
        assertEquals("cat", criterion.getCategory());
        assertEquals("type", criterion.getCriterionType());
        assertEquals("core", criterion.getAmbitionLevel());
        assertEquals("ID1", criterion.getId());
        assertEquals("name", criterion.getName());
        assertEquals(Arrays.asList("12345678", "87654321"), criterion.getRelevantCpvCodes());
        assertEquals("impact", criterion.getEnvironmentalImpactType());
        assertEquals("desc", criterion.getDescription());
        assertEquals("selType", criterion.getSelectionCriterionType());
    }

    @Test
    public void testAllArgsConstructor() {
        List<String> cpvs = Arrays.asList("12345678", "87654321");
        GppCriterion criterion = new GppCriterion(
                "doc", "eu", "cat", "type", "core", "ID1", "name", cpvs, "impact", "desc", "selType");
        assertEquals("doc", criterion.getGppDocument());
        assertEquals("eu", criterion.getGppSource());
        assertEquals("cat", criterion.getCategory());
        assertEquals("type", criterion.getCriterionType());
        assertEquals("core", criterion.getAmbitionLevel());
        assertEquals("ID1", criterion.getId());
        assertEquals("name", criterion.getName());
        assertEquals(cpvs, criterion.getRelevantCpvCodes());
        assertEquals("impact", criterion.getEnvironmentalImpactType());
        assertEquals("desc", criterion.getDescription());
        assertEquals("selType", criterion.getSelectionCriterionType());
    }

    @Test
    public void testIsApplicable_cpvAndAmbitionMatch() {
        List<String> cpvs = Arrays.asList("12345678", "87654321");
        GppCriterion criterion = new GppCriterion(
                "doc", "eu", "cat", "type", "core", "ID1", "name", Arrays.asList("12345678"), "impact", "desc",
                "selType");
        assertTrue(criterion.isApplicable(cpvs, "core"));
    }

    @Test
    public void testIsApplicable_cpvMatchAmbitionBoth() {
        List<String> cpvs = Arrays.asList("12345678");
        GppCriterion criterion = new GppCriterion(
                "doc", "eu", "cat", "type", "both", "ID1", "name", Arrays.asList("12345678"), "impact", "desc",
                "selType");
        assertTrue(criterion.isApplicable(cpvs, "core"));
        assertTrue(criterion.isApplicable(cpvs, "comprehensive"));
    }

    @Test
    public void testIsApplicable_noCpvMatch() {
        List<String> cpvs = Arrays.asList("99999999");
        GppCriterion criterion = new GppCriterion(
                "doc", "eu", "cat", "type", "core", "ID1", "name", Arrays.asList("12345678"), "impact", "desc",
                "selType");
        assertFalse(criterion.isApplicable(cpvs, "core"));
    }

    @Test
    public void testIsApplicable_noAmbitionMatch() {
        List<String> cpvs = Arrays.asList("12345678");
        GppCriterion criterion = new GppCriterion(
                "doc", "eu", "cat", "type", "core", "ID1", "name", Arrays.asList("12345678"), "impact", "desc",
                "selType");
        assertFalse(criterion.isApplicable(cpvs, "comprehensive"));
    }

    @Test
    public void testIsApplicable_emptyRelevantCpvs() {
        List<String> cpvs = Arrays.asList("12345678");
        GppCriterion criterion = new GppCriterion(
                "doc", "eu", "cat", "type", "core", "ID1", "name", Collections.emptyList(), "impact", "desc",
                "selType");
        assertFalse(criterion.isApplicable(cpvs, "core"));
    }

    @Test
    public void testToString() {
        GppCriterion criterion = new GppCriterion(
                "doc", "eu", "cat", "type", "core", "ID1", "name", Arrays.asList("12345678"), "impact", "desc",
                "selType");
        String str = criterion.toString();
        assertTrue(str.contains("GppCriterion{"));
        assertTrue(str.contains("gppDocument='doc'"));
        assertTrue(str.contains("ID1"));
    }

    @Test
    public void testGetFormattedAmbitionLevel() {
        GppCriterion criterion = new GppCriterion();
        criterion.setAmbitionLevel("both");
        assertEquals("core and comprehensive", criterion.getFormattedAmbitionLevel());
        criterion.setAmbitionLevel("core");
        assertEquals("core", criterion.getFormattedAmbitionLevel());
        criterion.setAmbitionLevel("comprehensive");
        assertEquals("comprehensive", criterion.getFormattedAmbitionLevel());
    }

}
