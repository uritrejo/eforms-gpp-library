package it.polimi.gpplib.model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GppDocumentTest {

    @Test
    public void testGettersAndSetters() {
        GppDocument doc = new GppDocument();
        doc.setName("Test Name");
        doc.setSource("Test Source");
        doc.setDocumentReference("Test Reference");
        LocalDateTime date = LocalDateTime.of(2020, 1, 1, 0, 0);
        doc.setPublicationDate(date);
        List<String> cpvs = Arrays.asList("12345678", "87654321");
        doc.setRelevantCpvCodes(cpvs);
        doc.setSummary("Test Summary");

        assertEquals("Test Name", doc.getName());
        assertEquals("Test Source", doc.getSource());
        assertEquals("Test Reference", doc.getDocumentReference());
        assertEquals(date, doc.getPublicationDate());
        assertEquals(cpvs, doc.getRelevantCpvCodes());
        assertEquals("Test Summary", doc.getSummary());
    }

    @Test
    public void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.of(2021, 5, 10, 12, 0);
        List<String> cpvs = Arrays.asList("11111111", "22222222");
        GppDocument doc = new GppDocument("Doc Name", "Doc Source", "Doc Ref", date, cpvs, "Doc Summary");

        assertEquals("Doc Name", doc.getName());
        assertEquals("Doc Source", doc.getSource());
        assertEquals("Doc Ref", doc.getDocumentReference());
        assertEquals(date, doc.getPublicationDate());
        assertEquals(cpvs, doc.getRelevantCpvCodes());
        assertEquals("Doc Summary", doc.getSummary());
    }

    @Test
    public void testIsApplicable_true() {
        List<String> docCpvs = Arrays.asList("12345678", "87654321");
        GppDocument doc = new GppDocument();
        doc.setRelevantCpvCodes(docCpvs);

        List<String> inputCpvs = Arrays.asList("00000000", "12345678");
        assertTrue(doc.isApplicable(inputCpvs));
    }

    @Test
    public void testIsApplicable_false() {
        List<String> docCpvs = Arrays.asList("12345678", "87654321");
        GppDocument doc = new GppDocument();
        doc.setRelevantCpvCodes(docCpvs);

        List<String> inputCpvs = Arrays.asList("99999999");
        assertFalse(doc.isApplicable(inputCpvs));
    }

    @Test
    public void testIsApplicable_emptyRelevantCpvs() {
        GppDocument doc = new GppDocument();
        doc.setRelevantCpvCodes(Collections.emptyList());

        List<String> inputCpvs = Arrays.asList("12345678");
        assertFalse(doc.isApplicable(inputCpvs));
    }

    @Test
    public void testToString() {
        LocalDateTime date = LocalDateTime.of(2022, 2, 2, 2, 2);
        List<String> cpvs = Arrays.asList("11111111");
        GppDocument doc = new GppDocument("Name", "Source", "Ref", date, cpvs, "Summary");
        String str = doc.toString();
        assertTrue(str.contains("GppDocument{"));
        assertTrue(str.contains("Name"));
        assertTrue(str.contains("Source"));
        assertTrue(str.contains("11111111"));
    }
}
