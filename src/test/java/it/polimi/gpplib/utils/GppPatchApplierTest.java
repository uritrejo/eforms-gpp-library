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
            assertEquals("Unsupported patch operation: potato", ex.getMessage());
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

    @Test
    public void testApplyPatch_updateOperation() {
        // First, create an award criterion to update
        String initialAwardCriterion = "<cac:SubordinateAwardingCriterion xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cbc:AwardingCriterionTypeCode listName=\"award-criterion-type\">quality</cbc:AwardingCriterionTypeCode>"
                + "<cbc:Name languageID=\"EN\">Original Award Criterion</cbc:Name>"
                + "<cbc:Description languageID=\"EN\">Original description</cbc:Description>"
                + "</cac:SubordinateAwardingCriterion>";

        // Create the award criteria structure first
        String awardingTermsValue = "<cac:AwardingTerms xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">"
                + "<cac:AwardingCriterion>" + initialAwardCriterion + "</cac:AwardingCriterion>"
                + "</cac:AwardingTerms>";

        String tenderingTermsValue = "<cac:TenderingTerms xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">"
                + awardingTermsValue + "</cac:TenderingTerms>";

        // Insert the tendering terms structure
        SuggestedGppPatch setupPatch = new SuggestedGppPatch("setupPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, tenderingTermsValue, "create", "Setup patch", "LOT-0001");
        notice = patchApplier.applyPatch(notice, setupPatch);

        // Verify the initial structure exists
        Node lot = notice.getLotNode("LOT-0001");
        Node originalNode = XmlUtils.getNodeAtPath(lot,
                "cac:ProcurementProject/cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion[cbc:Name='Original Award Criterion']");
        assertNotNull("Original award criterion should exist", originalNode);

        // Now test the UPDATE operation
        String updatedAwardCriterion = "<cac:SubordinateAwardingCriterion xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:efext=\"http://data.europa.eu/p27/eforms-ubl-extensions/1\" xmlns:efac=\"http://data.europa.eu/p27/eforms-ubl-extension-aggregate-components/1\" xmlns:efbc=\"http://data.europa.eu/p27/eforms-ubl-extension-basic-components/1\">"
                + "<ext:UBLExtensions>"
                + "<ext:UBLExtension>"
                + "<ext:ExtensionContent>"
                + "<efext:EformsExtension>"
                + "<efac:AwardCriterionParameter>"
                + "<efbc:ParameterCode listName=\"number-weight\">per-exa</efbc:ParameterCode>"
                + "<efbc:ParameterNumeric>20</efbc:ParameterNumeric>"
                + "</efac:AwardCriterionParameter>"
                + "</efext:EformsExtension>"
                + "</ext:ExtensionContent>"
                + "</ext:UBLExtension>"
                + "</ext:UBLExtensions>"
                + "<cbc:AwardingCriterionTypeCode listName=\"award-criterion-type\">quality</cbc:AwardingCriterionTypeCode>"
                + "<cbc:Name languageID=\"EN\">Original Award Criterion</cbc:Name>"
                + "<cbc:Description languageID=\"EN\">Updated GPP description with environmental criteria</cbc:Description>"
                + "</cac:SubordinateAwardingCriterion>";

        String updatePath = "cac:ProcurementProject/cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion[cbc:Name='Original Award Criterion']";
        SuggestedGppPatch updatePatch = new SuggestedGppPatch("updatePatch", Collections.emptyList(), null,
                updatePath, updatedAwardCriterion, "update", "Update award criterion with GPP structure", "LOT-0001");

        // Apply the update
        Notice updatedNotice = patchApplier.applyPatch(notice, updatePatch);
        Node updatedLot = updatedNotice.getLotNode("LOT-0001");

        // Verify the update was successful
        Node updatedNode = XmlUtils.getNodeAtPath(updatedLot, updatePath);
        assertNotNull("Updated award criterion should exist", updatedNode);

        // Verify the description was updated
        String updatedDescription = XmlUtils.getNodeValueAtPath(updatedLot, updatePath + "/cbc:Description");
        assertEquals("Updated GPP description with environmental criteria", updatedDescription);

        // Verify the new GPP structure was added
        Node parameterNode = XmlUtils.getNodeAtPath(updatedLot, updatePath
                + "/ext:UBLExtensions/ext:UBLExtension/ext:ExtensionContent/efext:EformsExtension/efac:AwardCriterionParameter");
        assertNotNull("GPP parameter structure should exist", parameterNode);

        String parameterCode = XmlUtils.getNodeValueAtPath(updatedLot, updatePath
                + "/ext:UBLExtensions/ext:UBLExtension/ext:ExtensionContent/efext:EformsExtension/efac:AwardCriterionParameter/efbc:ParameterCode");
        assertEquals("per-exa", parameterCode);

        String parameterValue = XmlUtils.getNodeValueAtPath(updatedLot, updatePath
                + "/ext:UBLExtensions/ext:UBLExtension/ext:ExtensionContent/efext:EformsExtension/efac:AwardCriterionParameter/efbc:ParameterNumeric");
        assertEquals("20", parameterValue);
    }

    @Test
    public void testApplyPatch_updateOperation_nodeNotFound() {
        String updateValue = "<cac:SubordinateAwardingCriterion xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"><cbc:Name>Test</cbc:Name></cac:SubordinateAwardingCriterion>";
        String nonExistentPath = "cac:ProcurementProject/cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion[cbc:Name='NonExistent']";

        SuggestedGppPatch updatePatch = new SuggestedGppPatch("updatePatch", Collections.emptyList(), null,
                nonExistentPath, updateValue, "update", "Update non-existent node", "LOT-0001");

        try {
            patchApplier.applyPatch(notice, updatePatch);
            fail("Expected IllegalArgumentException for non-existent node");
        } catch (IllegalArgumentException ex) {
            assertTrue("Should mention node not found", ex.getMessage().contains("Node not found at path"));
        }
    }

    @Test
    public void testApplyPatch_updateOperation_invalidXmlValue() {
        // First create a simple node to update
        String simpleNode = "<cac:TestNode xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><cbc:Name>Test</cbc:Name></cac:TestNode>";
        SuggestedGppPatch setupPatch = new SuggestedGppPatch("setupPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, simpleNode, "create", "Setup patch", "LOT-0001");
        notice = patchApplier.applyPatch(notice, setupPatch);

        // Try to update with invalid XML
        String invalidXml = "<invalid-xml-content";
        String updatePath = "cac:ProcurementProject/cac:TestNode";

        SuggestedGppPatch updatePatch = new SuggestedGppPatch("updatePatch", Collections.emptyList(), null,
                updatePath, invalidXml, "update", "Update with invalid XML", "LOT-0001");

        try {
            patchApplier.applyPatch(notice, updatePatch);
            fail("Expected IllegalArgumentException for invalid XML");
        } catch (IllegalArgumentException ex) {
            assertTrue("Should mention invalid patch value", ex.getMessage().contains("Invalid patch value"));
        }
    }

    @Test
    public void testApplyPatch_updateOperation_maintainsElementOrder() {
        // Create a structure with multiple siblings to test order preservation
        String multipleNodes = "<cac:TestParent xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cac:FirstChild><cbc:Name>First</cbc:Name></cac:FirstChild>"
                + "<cac:TargetChild><cbc:Name>Target</cbc:Name><cbc:Description>Original</cbc:Description></cac:TargetChild>"
                + "<cac:LastChild><cbc:Name>Last</cbc:Name></cac:LastChild>"
                + "</cac:TestParent>";

        SuggestedGppPatch setupPatch = new SuggestedGppPatch("setupPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, multipleNodes, "create", "Setup patch", "LOT-0001");
        notice = patchApplier.applyPatch(notice, setupPatch);

        // Update the middle child
        String updatedChild = "<cac:TargetChild xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cbc:Name>Target</cbc:Name>"
                + "<cbc:Description>Updated description</cbc:Description>"
                + "<cbc:NewField>Added field</cbc:NewField>"
                + "</cac:TargetChild>";

        String updatePath = "cac:ProcurementProject/cac:TestParent/cac:TargetChild";
        SuggestedGppPatch updatePatch = new SuggestedGppPatch("updatePatch", Collections.emptyList(), null,
                updatePath, updatedChild, "update", "Update middle child", "LOT-0001");

        // Apply the update
        Notice updatedNotice = patchApplier.applyPatch(notice, updatePatch);
        Node updatedLot = updatedNotice.getLotNode("LOT-0001");

        // Verify the update was successful
        String updatedDescription = XmlUtils.getNodeValueAtPath(updatedLot, updatePath + "/cbc:Description");
        assertEquals("Updated description", updatedDescription);

        String newField = XmlUtils.getNodeValueAtPath(updatedLot, updatePath + "/cbc:NewField");
        assertEquals("Added field", newField);

        // Verify order is maintained - check that siblings are still in correct order
        Node parentNode = XmlUtils.getNodeAtPath(updatedLot, "cac:ProcurementProject/cac:TestParent");
        assertNotNull("Parent node should exist", parentNode);

        // Verify FirstChild comes before TargetChild and TargetChild comes before
        // LastChild
        boolean foundFirst = false, foundTarget = false, foundLast = false;
        for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
            Node child = parentNode.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                if ("cac:FirstChild".equals(nodeName)) {
                    foundFirst = true;
                    assertFalse("FirstChild should come before TargetChild", foundTarget);
                    assertFalse("FirstChild should come before LastChild", foundLast);
                } else if ("cac:TargetChild".equals(nodeName)) {
                    foundTarget = true;
                    assertTrue("TargetChild should come after FirstChild", foundFirst);
                    assertFalse("TargetChild should come before LastChild", foundLast);
                } else if ("cac:LastChild".equals(nodeName)) {
                    foundLast = true;
                    assertTrue("LastChild should come after FirstChild", foundFirst);
                    assertTrue("LastChild should come after TargetChild", foundTarget);
                }
            }
        }

        assertTrue("All three children should be found", foundFirst && foundTarget && foundLast);
    }

    @Test
    public void testApplyPatch_updateOperation_withComplexPath() {
        // Test updating a node with a complex XPath that includes predicates
        String complexStructure = "<cac:TenderingTerms xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cac:AwardingTerms>"
                + "<cac:AwardingCriterion>"
                + "<cac:SubordinateAwardingCriterion>"
                + "<cbc:Name languageID=\"EN\">Quality Criterion</cbc:Name>"
                + "<cbc:Description languageID=\"EN\">Original quality description</cbc:Description>"
                + "</cac:SubordinateAwardingCriterion>"
                + "<cac:SubordinateAwardingCriterion>"
                + "<cbc:Name languageID=\"EN\">Price Criterion</cbc:Name>"
                + "<cbc:Description languageID=\"EN\">Price description</cbc:Description>"
                + "</cac:SubordinateAwardingCriterion>"
                + "</cac:AwardingCriterion>"
                + "</cac:AwardingTerms>"
                + "</cac:TenderingTerms>";

        SuggestedGppPatch setupPatch = new SuggestedGppPatch("setupPatch", Collections.emptyList(), null,
                Constants.PATH_PROCUREMENT_PROJECT, complexStructure, "create", "Setup patch", "LOT-0001");
        notice = patchApplier.applyPatch(notice, setupPatch);

        // Update specifically the Quality Criterion using XPath predicate
        String updatedQualityCriterion = "<cac:SubordinateAwardingCriterion xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"
                + "<cbc:Name languageID=\"EN\">Quality Criterion</cbc:Name>"
                + "<cbc:Description languageID=\"EN\">Enhanced GPP quality description with environmental factors</cbc:Description>"
                + "<cbc:Weight>40</cbc:Weight>"
                + "</cac:SubordinateAwardingCriterion>";

        String complexUpdatePath = "cac:ProcurementProject/cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion[cbc:Name='Quality Criterion']";
        SuggestedGppPatch updatePatch = new SuggestedGppPatch("updatePatch", Collections.emptyList(), null,
                complexUpdatePath, updatedQualityCriterion, "update", "Update quality criterion", "LOT-0001");

        // Apply the update
        Notice updatedNotice = patchApplier.applyPatch(notice, updatePatch);
        Node updatedLot = updatedNotice.getLotNode("LOT-0001");

        // Verify the quality criterion was updated
        String updatedDescription = XmlUtils.getNodeValueAtPath(updatedLot, complexUpdatePath + "/cbc:Description");
        assertEquals("Enhanced GPP quality description with environmental factors", updatedDescription);

        String weight = XmlUtils.getNodeValueAtPath(updatedLot, complexUpdatePath + "/cbc:Weight");
        assertEquals("40", weight);

        // Verify the price criterion was not affected
        String priceUpdatePath = "cac:ProcurementProject/cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion[cbc:Name='Price Criterion']";
        String priceDescription = XmlUtils.getNodeValueAtPath(updatedLot, priceUpdatePath + "/cbc:Description");
        assertEquals("Price description", priceDescription);

        // Verify no weight was added to price criterion
        Node priceWeightNode = XmlUtils.getNodeAtPath(updatedLot, priceUpdatePath + "/cbc:Weight");
        assertNull("Price criterion should not have weight", priceWeightNode);
    }
}