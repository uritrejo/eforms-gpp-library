package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppDocument;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GppDocumentsLoaderTest {

    @Test
    public void testLoadGppDocuments_withTestFile() throws Exception {
        String testFilePath = "domain_knowledge/test_gpp_criteria_docs.json";
        GppDocumentsLoader loader = new GppDocumentsLoader(testFilePath);
        List<GppDocument> docs = loader.loadGppDocuments();

        assertNotNull(docs);
        assertEquals(2, docs.size());

        GppDocument doc1 = docs.get(0);
        assertEquals("Doc1", doc1.getName());
        assertEquals("Source1", doc1.getSource());
        assertEquals("Ref1", doc1.getDocumentReference());
        assertNotNull(doc1.getPublicationDate());
        assertEquals(1, doc1.getRelevantCpvCodes().size());
        assertEquals("11111111", doc1.getRelevantCpvCodes().get(0));
        assertEquals("Summary1", doc1.getSummary());

        GppDocument doc2 = docs.get(1);
        assertEquals("Doc2", doc2.getName());
        assertEquals("Source2", doc2.getSource());
        assertEquals("Ref2", doc2.getDocumentReference());
        assertNotNull(doc2.getPublicationDate());
        assertEquals(2, doc2.getRelevantCpvCodes().size());
        assertEquals("22222222", doc2.getRelevantCpvCodes().get(0));
        assertEquals("33333333", doc2.getRelevantCpvCodes().get(1));
        assertEquals("Summary2", doc2.getSummary());
    }
}
