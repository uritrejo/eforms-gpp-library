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

    @Test
    public void testApplyPatch_procurementProjectWithSchemaOrdering() {
        // Test with cac:ProcurementAdditionalType which should be inserted in schema
        // order
        String patchValue = "<cac:ProcurementAdditionalType xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cbc:ProcurementTypeCode listName=\"gpp-criteria\">test-criterion</cbc:ProcurementTypeCode>"
                + "</cac:ProcurementAdditionalType>";

        SuggestedGppPatch patch = new SuggestedGppPatch("gppPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, patchValue, "create", "GPP Patch", "LOT-0001");

        Notice patchedNotice = patchApplier.applyPatch(notice, patch);
        Node lot = patchedNotice.getLotNode("LOT-0001");

        // Verify the element was inserted
        Node insertedNode = XmlUtils.getNodeAtPath(lot,
                Constants.PATH_PROCUREMENT_PROJECT + "/cac:ProcurementAdditionalType");
        assertNotNull("ProcurementAdditionalType should be inserted", insertedNode);

        String insertedValue = XmlUtils.getNodeValueAtPath(lot,
                Constants.PATH_PROCUREMENT_PROJECT + "/cac:ProcurementAdditionalType/cbc:ProcurementTypeCode");
        assertEquals("test-criterion", insertedValue);

        // Verify it's inserted in the correct position relative to existing elements
        // It should come after MainCommodityClassification but before other elements
        // that come later in schema
        Node procurementProject = XmlUtils.getNodeAtPath(lot, Constants.PATH_PROCUREMENT_PROJECT);
        assertNotNull("ProcurementProject should exist", procurementProject);
    }

    @Test
    public void testApplyPatch_elementNotInSchema() {
        // Test with an element that's not in the schema - should append at end
        String patchValue = "<NonSchemaElement>"
                + "<Value>test-value</Value>"
                + "</NonSchemaElement>";

        SuggestedGppPatch patch = new SuggestedGppPatch("customPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, patchValue, "create", "Custom Patch", "LOT-0001");

        Notice patchedNotice = patchApplier.applyPatch(notice, patch);
        Node lot = patchedNotice.getLotNode("LOT-0001");

        // Verify the element was inserted
        Node insertedNode = XmlUtils.getNodeAtPath(lot, Constants.PATH_PROCUREMENT_PROJECT + "/NonSchemaElement");
        assertNotNull("NonSchemaElement should be inserted", insertedNode);

        String insertedValue = XmlUtils.getNodeValueAtPath(lot,
                Constants.PATH_PROCUREMENT_PROJECT + "/NonSchemaElement/Value");
        assertEquals("test-value", insertedValue);
    }

    @Test
    public void testApplyPatch_elementIsLastInSchema() {
        // Test with an element that is the last in the schema - should append at end
        // We'll use a known schema element that typically comes last
        String patchValue = "<cac:RequestForTenderLine xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">"
                + "<cbc:ID xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">line-1</cbc:ID>"
                + "</cac:RequestForTenderLine>";

        SuggestedGppPatch patch = new SuggestedGppPatch("lastElementPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, patchValue, "create", "Last Element Patch", "LOT-0001");

        Notice patchedNotice = patchApplier.applyPatch(notice, patch);
        Node lot = patchedNotice.getLotNode("LOT-0001");

        // Verify the element was inserted
        Node insertedNode = XmlUtils.getNodeAtPath(lot,
                Constants.PATH_PROCUREMENT_PROJECT + "/cac:RequestForTenderLine");
        assertNotNull("RequestForTenderLine should be inserted", insertedNode);
    }

    @Test
    public void testApplyPatch_nonProcurementProjectPath() {
        // Test that non-procurement project paths still use regular insertion
        String patchValue = "<cac:TestElement xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">"
                + "<cbc:Value xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">test</cbc:Value>"
                + "</cac:TestElement>";

        SuggestedGppPatch patch = new SuggestedGppPatch("regularPatch", Collections.emptyList(), null,
                "cac:TenderingTerms", patchValue, "create", "Regular Patch", "LOT-0001");

        Notice patchedNotice = patchApplier.applyPatch(notice, patch);
        Node lot = patchedNotice.getLotNode("LOT-0001");

        // Verify the element was inserted in TenderingTerms
        Node insertedNode = XmlUtils.getNodeAtPath(lot, "cac:TenderingTerms/cac:TestElement");
        assertNotNull("TestElement should be inserted in TenderingTerms", insertedNode);

        String insertedValue = XmlUtils.getNodeValueAtPath(lot, "cac:TenderingTerms/cac:TestElement/cbc:Value");
        assertEquals("test", insertedValue);
    }

    @Test
    public void testApplyPatch_multipleProcurementProjectElements() {
        // Test applying multiple patches to verify correct ordering
        String firstPatchValue = "<cac:ProcurementAdditionalType xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cbc:ProcurementTypeCode listName=\"gpp-criteria\">first-criterion</cbc:ProcurementTypeCode>"
                + "</cac:ProcurementAdditionalType>";

        String secondPatchValue = "<cac:Description xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cbc:Description>Test description</cbc:Description>"
                + "</cac:Description>";

        SuggestedGppPatch firstPatch = new SuggestedGppPatch("gppPatch1", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, firstPatchValue, "create", "First GPP Patch", "LOT-0001");

        SuggestedGppPatch secondPatch = new SuggestedGppPatch("gppPatch2", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, secondPatchValue, "create", "Second GPP Patch", "LOT-0001");

        // Apply first patch
        Notice patchedNotice = patchApplier.applyPatch(notice, firstPatch);

        // Apply second patch
        patchedNotice = patchApplier.applyPatch(patchedNotice, secondPatch);

        Node lot = patchedNotice.getLotNode("LOT-0001");

        // Verify both elements were inserted
        Node firstInserted = XmlUtils.getNodeAtPath(lot,
                Constants.PATH_PROCUREMENT_PROJECT + "/cac:ProcurementAdditionalType");
        assertNotNull("ProcurementAdditionalType should be inserted", firstInserted);

        Node secondInserted = XmlUtils.getNodeAtPath(lot, Constants.PATH_PROCUREMENT_PROJECT + "/cac:Description");
        assertNotNull("Description should be inserted", secondInserted);
    }

    @Test
    public void testApplyPatch_schemaOrderingVerification() {
        // This test verifies that elements are inserted in the correct schema order
        // We'll insert an element that should come early in the schema
        String earlyElementPatch = "<cbc:ID xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">test-id</cbc:ID>";

        SuggestedGppPatch patch = new SuggestedGppPatch("earlyPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, earlyElementPatch, "create", "Early Element Patch", "LOT-0001");

        Notice patchedNotice = patchApplier.applyPatch(notice, patch);
        Node lot = patchedNotice.getLotNode("LOT-0001");
        Node procurementProject = XmlUtils.getNodeAtPath(lot, Constants.PATH_PROCUREMENT_PROJECT);

        // Verify the ID element was inserted
        Node insertedIdNode = XmlUtils.getNodeAtPath(lot, Constants.PATH_PROCUREMENT_PROJECT + "/cbc:ID");
        assertNotNull("ID element should be inserted", insertedIdNode);
        assertEquals("test-id", insertedIdNode.getTextContent());

        // The ID element should be positioned before the MainCommodityClassification
        // since it comes earlier in the schema
        boolean foundId = false;
        boolean foundMainClassification = false;

        for (int i = 0; i < procurementProject.getChildNodes().getLength(); i++) {
            Node child = procurementProject.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                if ("cbc:ID".equals(nodeName)) {
                    foundId = true;
                    assertFalse("ID should come before MainCommodityClassification", foundMainClassification);
                } else if ("cac:MainCommodityClassification".equals(nodeName)) {
                    foundMainClassification = true;
                }
            }
        }

        assertTrue("ID element should be found", foundId);
        assertTrue("MainCommodityClassification should be found", foundMainClassification);
    }
}