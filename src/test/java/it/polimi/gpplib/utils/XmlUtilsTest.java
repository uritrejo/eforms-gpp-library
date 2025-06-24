package it.polimi.gpplib.utils;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.polimi.gpplib.model.Constants;

import static org.junit.Assert.*;

public class XmlUtilsTest {

    @Test
    public void testGetAsXmlString_existingFile() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        assertNotNull("XML string should not be null for existing resource", xml);
        assertTrue("XML string should contain ContractNotice root element", xml.contains("<ContractNotice"));
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testGetAsXmlString_nonExistingFile() {
        XmlUtils.getAsXmlString("non_existing_file.xml");
    }

    @Test
    public void testLoadDocument_validXml() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        assertNotNull("Document should not be null for valid XML", doc);
        assertEquals("ContractNotice", doc.getDocumentElement().getLocalName());
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testLoadDocument_invalidXml() {
        String invalidXml = "<root><unclosed></root>";
        XmlUtils.loadDocument(invalidXml);
    }

    // ??++
    @Test
    public void testGetNodeAtPath_existingNode() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        assertNotNull("Node should be found at given XPath", node);
        assertEquals("ProcurementProjectLot", node.getLocalName());
    }

    // ??++ pass in a bs path...
    @Test
    public void testGetNodeAtPath_nonExistingNode() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting");
        assertNull("Node should be null for non-existing XPath", node);
    }

    @Test
    public void testGetNodeValueAtPath_existingNode() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        String value = XmlUtils.getNodeValueAtPath(doc.getDocumentElement(),
                Constants.PATH_LOT + "/" + Constants.PATH_IN_LOT_ID);
        assertEquals("LOT-0001", value);
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testGetNodeValueAtPath_nonExistingNode() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        XmlUtils.getNodeValueAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting");
    }

    @Test
    public void testGetNodesAtPath_existingNodes() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        NodeList nodes = XmlUtils.getNodesAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        assertNotNull("NodeList should not be null", nodes);
        assertTrue("Should find at least one ProcurementProjectLot node", nodes.getLength() > 0);
    }

    @Test
    public void testGetNodesAtPath_nonExistingNodes() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        NodeList nodes = XmlUtils.getNodesAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting");
        assertNotNull("NodeList should not be null even if no nodes found", nodes);
        assertEquals(0, nodes.getLength());
    }

    @Test
    public void testDoesNodeExistAtPath_true() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        assertTrue(XmlUtils.doesNodeExistAtPath(doc.getDocumentElement(), Constants.PATH_LOT));
    }

    @Test
    public void testDoesNodeExistAtPath_false() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        assertFalse(XmlUtils.doesNodeExistAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting"));
    }

    @Test
    public void testDocToString_roundTrip() {
        String xml = XmlUtils.getAsXmlString("test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        String xmlOut = XmlUtils.docToString(doc);
        assertNotNull(xmlOut);
        assertTrue(xmlOut.contains("ContractNotice"));
    }

    @Test
    public void testInsertIntoNode() {

        Document docA = XmlUtils.loadDocument("<A></A>");
        Document docB = XmlUtils.loadDocument("<B></B>");
        Node nodeA = docA.getDocumentElement();
        Node nodeB = docB.getDocumentElement();
        XmlUtils.insertIntoNode(nodeA, nodeB);
        assertEquals("A", nodeA.getLocalName());
        boolean hasB = false;
        for (int i = 0; i < nodeA.getChildNodes().getLength(); i++) {
            Node child = nodeA.getChildNodes().item(i);
            if ("B".equals(child.getLocalName())) {
                hasB = true;
                break;
            }
        }
        assertTrue("Node A should have B as a child after insertion", hasB);
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testInsertIntoNode_nullParentOrChild() {
        XmlUtils.insertIntoNode(null, null);
    }
}