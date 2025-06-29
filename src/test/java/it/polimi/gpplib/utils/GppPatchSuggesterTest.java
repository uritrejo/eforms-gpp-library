package it.polimi.gpplib.utils;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppPatch;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.SuggestedGppPatch;

public class GppPatchSuggesterTest {
    @Test
    public void testSuggestGppPatches_noSuggestedCriteria() {
        List<GppCriterion> gppCriteria = new ArrayList<>();
        List<GppPatch> gppPatches = new ArrayList<>();
        GppPatchSuggester suggester = new GppPatchSuggester(gppCriteria, gppPatches);
        String noticeXmlString = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
        Notice notice = new Notice(noticeXmlString);
        List<SuggestedGppCriterion> suggestedCriteria = new ArrayList<>();
        List<SuggestedGppPatch> suggestedPatches = suggester.suggestGppPatches(notice, suggestedCriteria);
        assertTrue(suggestedPatches.isEmpty());
    }

    @Test
    public void testSuggestGppPatches_manySuggestions() throws IOException {
        /*
         * We want to test a few things here:
         * - one criteria source patch per source
         * - one env-imp type patch per env-imp
         * - one strategic procurment patch
         * - one patch for each of the criterion types
         * - parent patches get inserted
         */

        List<GppCriterion> gppCriteria = new ArrayList<>();
        gppCriteria.add(new GppCriterion(
                "gpp-doc-1", "eu", "", "award criteria", "core", "AC1", "Award Criterion 1",
                List.of("30000000"), "env-imp-type-1", "description for AC1", null));
        gppCriteria.add(new GppCriterion(
                "gpp-doc-1", "eu", "", "selection criteria", "core", "SC1", "Selection Criterion 1",
                List.of("30000000"), "env-imp-type-2", "description for SC1", "selection-criterion-type-1"));
        gppCriteria.add(new GppCriterion(
                "gpp-doc-2", "national", "", "technical specification", "core", "TS1", "Technical Specification 1",
                List.of("30000000"), "env-imp-type-1", "description for TS1", null));
        gppCriteria.add(new GppCriterion(
                "gpp-doc-2", "national", "", "contract performing clause", "core", "CPC1",
                "Contract Performance Clause 1", List.of("30000000"), "env-imp-type-2", "description for CPC1", null));

        // we need the real GPP Patch data, so we use the loader
        GppPatchesLoader patchesLoader = new GppPatchesLoader("domain_knowledge/real_gpp_patches_data.json");
        List<GppPatch> gppPatches = patchesLoader.loadGppPatches();

        GppPatchSuggester suggester = new GppPatchSuggester(gppCriteria, gppPatches);
        String noticeXmlString = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
        Notice notice = new Notice(noticeXmlString);
        // we basically suggest all the criteria we have
        List<SuggestedGppCriterion> suggestedCriteria = new ArrayList<>();
        for (GppCriterion crit : gppCriteria) {
            suggestedCriteria.add(new SuggestedGppCriterion(
                    crit.getGppDocument(),
                    crit.getCategory(),
                    crit.getCriterionType(),
                    crit.getAmbitionLevel(),
                    crit.getId(),
                    crit.getName(),
                    crit.getRelevantCpvCodes(),
                    crit.getRelevantCpvCodes(),
                    "LOT-0001"));
        }

        List<SuggestedGppPatch> suggestedPatches = suggester.suggestGppPatches(notice, suggestedCriteria);

        // we expect one patch for each GPP source
        assertPatchExists(suggestedPatches, "Green Public Procurement Criteria - eu");
        assertPatchExists(suggestedPatches, "Green Public Procurement Criteria - national");

        // we expect one patch for each environmental impact type
        assertPatchExists(suggestedPatches, "Green Procurement - env-imp-type-1");
        assertPatchExists(suggestedPatches, "Green Procurement - env-imp-type-2");

        // we expect one strategic procurement patch
        assertPatchExists(suggestedPatches, "Strategic Procurement: Reduction of environmental impacts");

        // we expect one patch for each criterion
        assertPatchExists(suggestedPatches, "award criteria --- AC1: Award Criterion 1");
        assertPatchExists(suggestedPatches, "selection criteria --- SC1: Selection Criterion 1");
        assertPatchExists(suggestedPatches, "technical specification --- TS1: Technical Specification 1");
        assertPatchExists(suggestedPatches, "contract performing clause --- CPC1: Contract Performance Clause 1");

        // we expect parent patches to be inserted (for the award criteria)
        assertPatchExists(suggestedPatches, "Tendering Terms");
        assertPatchExists(suggestedPatches, "Awarding Terms");
        assertPatchExists(suggestedPatches, "Award Criteria");
        assertPatchExists(suggestedPatches, "Tendering Terms - Eforms Extension");

        // we ensure that there are no duplicates, particularly for the parent patches
        assertTrue(suggestedPatches.size() == 13);
    }

    private void assertPatchExists(List<SuggestedGppPatch> suggestedPatches, String patchName) {
        assertTrue(suggestedPatches.stream().anyMatch(patch -> patch.getName().equals(patchName)));
    }
}
