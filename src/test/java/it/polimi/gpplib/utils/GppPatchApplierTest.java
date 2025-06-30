package it.polimi.gpplib.utils;

import org.junit.Test;
import org.w3c.dom.Node;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.Collections;

import static org.junit.Assert.*;

public class GppPatchApplierTest {

    private GppPatchApplier patchApplier;
    private Notice notice;

    @org.junit.Before
    public void setUp() {
        String noticeXmlString = XmlUtils.getAsXmlString("test_notices/test_notice_minimal.xml");
        notice = new Notice(noticeXmlString);
        patchApplier = new GppPatchApplier(new EFormsSdkWrapper());
    }

    @Test
    public void testApplyPatch_nullPatch() {
        try {
            patchApplier.applyPatch(notice, null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Notice and patch must not be null", ex.getMessage());
        }
    }

    @Test
    public void testApplyPatch_invalidOp() {
        try {
            SuggestedGppPatch patch = new SuggestedGppPatch("testPatch", Collections.emptyList(), null,
                    Constants.PATH_PROCUREMENT_PROJECT, "<value>potato</value>", "potato", "Test patch", "LOT-0001");
            patchApplier.applyPatch(notice, patch);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid patch operation: potato", ex.getMessage());
        }
    }

    @Test
    public void testApplyPatch_invalidLotID() {
        try {
            SuggestedGppPatch patch = new SuggestedGppPatch("testPatch", Collections.emptyList(), null,
                    Constants.PATH_PROCUREMENT_PROJECT, "<value>potato</value>", "create", "Test patch", "LOT-0009");
            patchApplier.applyPatch(notice, patch);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Lot not found for id: LOT-0009", ex.getMessage());
        }
    }

    @Test
    public void testApplyPatch_invalidPath() {
        try {
            SuggestedGppPatch patch = new SuggestedGppPatch("testPatch", Collections.emptyList(), null,
                    "potato/unicorn", "<value>potato</value>", "create", "Test patch", "LOT-0001");
            patchApplier.applyPatch(notice, patch);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid patch path: potato/unicorn", ex.getMessage());
        }
    }

    @Test
    public void testApplyPatch_invalidValue() {
        try {
            SuggestedGppPatch patch = new SuggestedGppPatch("testPatch", Collections.emptyList(), null,
                    Constants.PATH_PROCUREMENT_PROJECT, "<hola....", "create", "Test patch", "LOT-0001");
            patchApplier.applyPatch(notice, patch);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid patch value: <hola....", ex.getMessage());
        }
    }

    @Test
    public void testApplyPatch_successfulPatch() {
        SuggestedGppPatch patch = new SuggestedGppPatch("testPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, "<value>potato</value>", "create", "Test patch", "LOT-0001");
        Notice patchedNotice = patchApplier.applyPatch(notice, patch);
        Node lot = patchedNotice.getLotNode("LOT-0001");
        String insertedValue = XmlUtils.getNodeValueAtPath(lot, Constants.PATH_PROCUREMENT_PROJECT + "/value");
        assertEquals("potato", insertedValue);
    }
}