package it.polimi.gpplib.model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GppAnalysisResultTest {

    @Test
    public void testNoArgsConstructorAndSetters() {
        GppAnalysisResult result = new GppAnalysisResult();

        GppDocument doc = new GppDocument("Doc1", "Source1", "Ref1", LocalDateTime.of(2020, 1, 1, 0, 0),
                Collections.singletonList("11111111"), "Summary1");
        SuggestedGppCriterion crit = new SuggestedGppCriterion("Doc1", "cat", "type", "core", "ID1", "Name1",
                Collections.singletonList("11111111"), Collections.singletonList("11111111"), "LOT-1");

        result.setRelevantGppDocuments(Collections.singletonList(doc));
        result.setSuggestedGppCriteria(Collections.singletonList(crit));

        assertEquals(1, result.getRelevantGppDocuments().size());
        assertEquals(doc, result.getRelevantGppDocuments().get(0));
        assertEquals(1, result.getSuggestedGppCriteria().size());
        assertEquals(crit, result.getSuggestedGppCriteria().get(0));
    }

    @Test
    public void testAllArgsConstructorAndGetters() {
        GppDocument doc1 = new GppDocument("Doc1", "Source1", "Ref1", LocalDateTime.of(2020, 1, 1, 0, 0),
                Arrays.asList("11111111", "22222222"), "Summary1");
        GppDocument doc2 = new GppDocument("Doc2", "Source2", "Ref2", LocalDateTime.of(2021, 2, 2, 0, 0),
                Collections.singletonList("33333333"), "Summary2");
        SuggestedGppCriterion crit1 = new SuggestedGppCriterion("Doc1", "catA", "typeA", "core", "ID1", "NameA",
                Arrays.asList("11111111", "22222222"), Arrays.asList("11111111"), "LOT-1");
        SuggestedGppCriterion crit2 = new SuggestedGppCriterion("Doc2", "catB", "typeB", "comprehensive", "ID2",
                "NameB", Collections.singletonList("33333333"), Collections.singletonList("33333333"), "LOT-2");

        List<GppDocument> docs = Arrays.asList(doc1, doc2);
        List<SuggestedGppCriterion> crits = Arrays.asList(crit1, crit2);

        GppAnalysisResult result = new GppAnalysisResult(docs, crits);

        assertEquals(docs, result.getRelevantGppDocuments());
        assertEquals(crits, result.getSuggestedGppCriteria());
    }
}
