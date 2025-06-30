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
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        assertNotNull("XML string should not be null for existing resource", xml);
        assertTrue("XML string should contain ContractNotice root element", xml.contains("<ContractNotice"));
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testGetAsXmlString_nonExistingFile() {
        XmlUtils.getAsXmlString("non_existing_file.xml");
    }

    @Test
    public void testLoadDocument_validXml() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        assertNotNull("Document should not be null for valid XML", doc);
        assertEquals("ContractNotice", doc.getDocumentElement().getLocalName());
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testLoadDocument_invalidXml() {
        String invalidXml = "<root><unclosed></root>";
        XmlUtils.loadDocument(invalidXml);
    }

    @Test
    public void testGetNodeAtPath_existingNode() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        assertNotNull("Node should be found at given XPath", node);
        assertEquals("ProcurementProjectLot", node.getLocalName());
    }

    @Test
    public void testGetNodeAtPath_nonExistingNode() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting");
        assertNull("Node should be null for non-existing XPath", node);
    }

    @Test
    public void testGetNodeValueAtPath_existingNode() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        String value = XmlUtils.getNodeValueAtPath(doc.getDocumentElement(),
                Constants.PATH_LOT + "/" + Constants.PATH_IN_LOT_ID);
        assertEquals("LOT-0001", value);
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testGetNodeValueAtPath_nonExistingNode() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        XmlUtils.getNodeValueAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting");
    }

    @Test
    public void testGetNodesAtPath_existingNodes() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        NodeList nodes = XmlUtils.getNodesAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        assertNotNull("NodeList should not be null", nodes);
        assertTrue("Should find at least one ProcurementProjectLot node", nodes.getLength() > 0);
    }

    @Test
    public void testGetNodesAtPath_nonExistingNodes() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        NodeList nodes = XmlUtils.getNodesAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting");
        assertNotNull("NodeList should not be null even if no nodes found", nodes);
        assertEquals(0, nodes.getLength());
    }

    @Test
    public void testDoesNodeExistAtPath_true() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        assertTrue(XmlUtils.doesNodeExistAtPath(doc.getDocumentElement(), Constants.PATH_LOT));
    }

    @Test
    public void testDoesNodeExistAtPath_false() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);
        assertFalse(XmlUtils.doesNodeExistAtPath(doc.getDocumentElement(), "cac:ProcurementProject/cbc:NonExisting"));
    }

    @Test
    public void testDocToString_roundTrip() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
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

    @Test
    public void testNamespaceContext_getNamespaceURI() {
        // Should return the correct URI for a known prefix
        String uri = XmlUtils.namespaceCtx.getNamespaceURI("cac");
        assertEquals(Constants.NAMESPACE_MAP.get("cac"), uri);

        // Should return "" for an unknown prefix
        String unknownUri = XmlUtils.namespaceCtx.getNamespaceURI("unknownPrefix");
        assertEquals("", unknownUri);
    }

    @Test
    public void testNamespaceContext_getPrefix() {
        // Should return the correct prefix for a known URI
        String prefix = XmlUtils.namespaceCtx.getPrefix(Constants.NAMESPACE_MAP.get("cac"));
        assertEquals("cac", prefix);

        // Should return null for an unknown URI
        String unknownPrefix = XmlUtils.namespaceCtx.getPrefix("http://unknown/uri");
        assertNull(unknownPrefix);
    }

    @Test
    public void testNamespaceContext_getPrefixes() {
        // Should return a non-empty iterator for a known URI
        var iterator = XmlUtils.namespaceCtx.getPrefixes(Constants.NAMESPACE_MAP.get("cac"));
        assertTrue(iterator.hasNext());
        assertEquals("cac", iterator.next());

        // Should return an empty iterator for an unknown URI
        var emptyIterator = XmlUtils.namespaceCtx.getPrefixes("http://unknown/uri");
        assertFalse(emptyIterator.hasNext());
    }

    @Test
    public void testRemoveNodeAtPath_existingNode() {
        // Create a test document with a removable node
        String testXml = "<root><parent><child>content</child></parent></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Verify the node exists before removal
        Node childNode = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "parent/child");
        assertNotNull("Child node should exist before removal", childNode);

        // Remove the node
        boolean removed = XmlUtils.removeNodeAtPath(doc.getDocumentElement(), "parent/child");
        assertTrue("Should return true when node is successfully removed", removed);

        // Verify the node no longer exists
        Node removedNode = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "parent/child");
        assertNull("Child node should not exist after removal", removedNode);

        // Verify parent still exists
        Node parentNode = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "parent");
        assertNotNull("Parent node should still exist after child removal", parentNode);
    }

    @Test
    public void testRemoveNodeAtPath_nonExistingNode() {
        String testXml = "<root><parent><child>content</child></parent></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Try to remove a non-existing node
        boolean removed = XmlUtils.removeNodeAtPath(doc.getDocumentElement(), "parent/nonexistent");
        assertFalse("Should return false when no node is found to remove", removed);
    }

    @Test
    public void testRemoveNodeAtPath_detachedContextNode() {
        String testXml = "<root><child>content</child></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Get a child node and detach it from its parent
        Node childNode = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "child");
        Node parentNode = childNode.getParentNode();
        parentNode.removeChild(childNode); // Now childNode has no parent and is detached

        // Try to search for something from this detached node context
        boolean removed = XmlUtils.removeNodeAtPath(childNode, "nonexistent");
        assertFalse("Should return false when no node is found", removed);
    }

    @Test
    public void testRemoveNodeAtPath_successfulRemoval() {
        String testXml = "<root><child>content</child></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Test normal successful removal
        boolean removed = XmlUtils.removeNodeAtPath(doc.getDocumentElement(), "child");
        assertTrue("Should successfully remove child node", removed);

        // Verify removal
        Node removedChild = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "child");
        assertNull("Child should no longer exist", removedChild);
    }

    @Test
    public void testRemoveNodeAtPath_rootElementFromDocument() {
        String testXml = "<root><child>content</child></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Remove root element from document context - this should work since Document
        // is parent of root
        boolean removed = XmlUtils.removeNodeAtPath(doc, "root");
        assertTrue("Should successfully remove root element from document", removed);

        // Verify the document no longer has the root element
        assertNull("Document should no longer have root element", doc.getDocumentElement());
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testRemoveNodeAtPath_nullRoot() {
        XmlUtils.removeNodeAtPath(null, "some/path");
    }

    @Test
    public void testRemoveNodeAtPath_withNamespaces() {
        // Load the actual test notice which has namespaces
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        Document doc = XmlUtils.loadDocument(xml);

        // First verify a lot exists
        Node lotNode = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        assertNotNull("Lot node should exist before removal", lotNode);

        // Remove the lot node
        boolean removed = XmlUtils.removeNodeAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        assertTrue("Should successfully remove the lot node", removed);

        // Verify it's gone
        Node removedLotNode = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        assertNull("Lot node should not exist after removal", removedLotNode);
    }

    @Test
    public void testRemoveNodeAtPath_multipleMatches() {
        // Create XML with multiple child nodes of same name
        String testXml = "<root><parent><item>1</item><item>2</item><item>3</item></parent></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Remove first item (XPath should match the first one)
        boolean removed = XmlUtils.removeNodeAtPath(doc.getDocumentElement(), "parent/item");
        assertTrue("Should successfully remove first matching node", removed);

        // Verify there are still item nodes left
        NodeList remainingItems = XmlUtils.getNodesAtPath(doc.getDocumentElement(), "parent/item");
        assertEquals("Should have 2 item nodes remaining", 2, remainingItems.getLength());
    }

    @Test
    public void testInsertIntoNodeBefore_withExistingReferenceNode() {
        // Create a test document with ordered elements
        String testXml = "<root><a>A</a><d>D</d><e>E</e></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Create a new node to insert
        Document newDoc = XmlUtils.loadDocument("<c>C</c>");
        Node newNode = newDoc.getDocumentElement();

        // Insert before d or e (d should be found first)
        java.util.List<String> before = java.util.Arrays.asList("d", "e");
        XmlUtils.insertIntoNodeBefore(doc.getDocumentElement(), newNode, before);

        // Verify the order is now a, c, d, e
        NodeList children = doc.getDocumentElement().getChildNodes();
        java.util.List<String> actualOrder = new java.util.ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                actualOrder.add(child.getLocalName());
            }
        }

        assertEquals("Should have 4 elements", 4, actualOrder.size());
        assertEquals("First element should be 'a'", "a", actualOrder.get(0));
        assertEquals("Second element should be 'c' (inserted)", "c", actualOrder.get(1));
        assertEquals("Third element should be 'd'", "d", actualOrder.get(2));
        assertEquals("Fourth element should be 'e'", "e", actualOrder.get(3));
    }

    @Test
    public void testInsertIntoNodeBefore_withSecondReferenceNode() {
        // Create a test document where first reference doesn't exist
        String testXml = "<root><a>A</a><e>E</e></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Create a new node to insert
        Document newDoc = XmlUtils.loadDocument("<c>C</c>");
        Node newNode = newDoc.getDocumentElement();

        // Insert before d or e (d doesn't exist, so should insert before e)
        java.util.List<String> before = java.util.Arrays.asList("d", "e");
        XmlUtils.insertIntoNodeBefore(doc.getDocumentElement(), newNode, before);

        // Verify the order is now a, c, e
        NodeList children = doc.getDocumentElement().getChildNodes();
        java.util.List<String> actualOrder = new java.util.ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                actualOrder.add(child.getLocalName());
            }
        }

        assertEquals("Should have 3 elements", 3, actualOrder.size());
        assertEquals("First element should be 'a'", "a", actualOrder.get(0));
        assertEquals("Second element should be 'c' (inserted)", "c", actualOrder.get(1));
        assertEquals("Third element should be 'e'", "e", actualOrder.get(2));
    }

    @Test
    public void testInsertIntoNodeBefore_noReferenceNodesExist() {
        // Create a test document with some elements
        String testXml = "<root><a>A</a><b>B</b></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Create a new node to insert
        Document newDoc = XmlUtils.loadDocument("<c>C</c>");
        Node newNode = newDoc.getDocumentElement();

        // Insert before non-existing elements (should append at end)
        java.util.List<String> before = java.util.Arrays.asList("x", "y", "z");
        XmlUtils.insertIntoNodeBefore(doc.getDocumentElement(), newNode, before);

        // Verify the order is now a, b, c (appended at end)
        NodeList children = doc.getDocumentElement().getChildNodes();
        java.util.List<String> actualOrder = new java.util.ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                actualOrder.add(child.getLocalName());
            }
        }

        assertEquals("Should have 3 elements", 3, actualOrder.size());
        assertEquals("First element should be 'a'", "a", actualOrder.get(0));
        assertEquals("Second element should be 'b'", "b", actualOrder.get(1));
        assertEquals("Third element should be 'c' (appended)", "c", actualOrder.get(2));
    }

    @Test
    public void testInsertIntoNodeBefore_emptyBeforeList() {
        // Create a test document
        String testXml = "<root><a>A</a><b>B</b></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Create a new node to insert
        Document newDoc = XmlUtils.loadDocument("<c>C</c>");
        Node newNode = newDoc.getDocumentElement();

        // Insert with empty before list (should append at end)
        java.util.List<String> before = new java.util.ArrayList<>();
        XmlUtils.insertIntoNodeBefore(doc.getDocumentElement(), newNode, before);

        // Verify the order is now a, b, c (appended at end)
        NodeList children = doc.getDocumentElement().getChildNodes();
        java.util.List<String> actualOrder = new java.util.ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                actualOrder.add(child.getLocalName());
            }
        }

        assertEquals("Should have 3 elements", 3, actualOrder.size());
        assertEquals("Third element should be 'c' (appended)", "c", actualOrder.get(2));
    }

    @Test
    public void testInsertIntoNodeBefore_nullBeforeList() {
        // Create a test document
        String testXml = "<root><a>A</a><b>B</b></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Create a new node to insert
        Document newDoc = XmlUtils.loadDocument("<c>C</c>");
        Node newNode = newDoc.getDocumentElement();

        // Insert with null before list (should append at end)
        XmlUtils.insertIntoNodeBefore(doc.getDocumentElement(), newNode, null);

        // Verify the node was inserted at the end
        NodeList children = doc.getDocumentElement().getChildNodes();
        java.util.List<String> actualOrder = new java.util.ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                actualOrder.add(child.getLocalName());
            }
        }

        assertEquals("Should have 3 elements", 3, actualOrder.size());
        assertEquals("Third element should be 'c' (appended)", "c", actualOrder.get(2));
    }

    @Test
    public void testInsertIntoNodeBefore_withXPathExpressions() {
        // Create a more complex test document with nested structure
        String testXml = "<root><section><item id='1'>Item 1</item><item id='3'>Item 3</item></section></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Create a new node to insert
        Document newDoc = XmlUtils.loadDocument("<item id='2'>Item 2</item>");
        Node newNode = newDoc.getDocumentElement();

        // Get the section node to insert into
        Node sectionNode = XmlUtils.getNodeAtPath(doc.getDocumentElement(), "section");

        // Insert before item with id='3' using XPath relative to section
        java.util.List<String> before = java.util.Arrays.asList("item[@id='3']", "item[@id='4']");
        XmlUtils.insertIntoNodeBefore(sectionNode, newNode, before);

        // Verify the insertion happened in the section
        NodeList items = XmlUtils.getNodesAtPath(doc.getDocumentElement(), "section/item");
        assertEquals("Should have 3 items in section", 3, items.getLength());

        // Verify the order by checking id attributes
        Node secondItem = items.item(1);
        assertEquals("Second item should have id='2'", "2",
                secondItem.getAttributes().getNamedItem("id").getNodeValue());
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testInsertIntoNodeBefore_nullParent() {
        Document newDoc = XmlUtils.loadDocument("<c>C</c>");
        Node newNode = newDoc.getDocumentElement();
        java.util.List<String> before = java.util.Arrays.asList("d");

        XmlUtils.insertIntoNodeBefore(null, newNode, before);
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testInsertIntoNodeBefore_nullNewChild() {
        String testXml = "<root><a>A</a></root>";
        Document doc = XmlUtils.loadDocument(testXml);
        java.util.List<String> before = java.util.Arrays.asList("d");

        XmlUtils.insertIntoNodeBefore(doc.getDocumentElement(), null, before);
    }

    @Test
    public void testInsertIntoNodeBefore_insertAtBeginning() {
        // Create a test document
        String testXml = "<root><b>B</b><c>C</c></root>";
        Document doc = XmlUtils.loadDocument(testXml);

        // Create a new node to insert
        Document newDoc = XmlUtils.loadDocument("<a>A</a>");
        Node newNode = newDoc.getDocumentElement();

        // Insert before b (should be at the beginning)
        java.util.List<String> before = java.util.Arrays.asList("b");
        XmlUtils.insertIntoNodeBefore(doc.getDocumentElement(), newNode, before);

        // Verify the order is now a, b, c
        NodeList children = doc.getDocumentElement().getChildNodes();
        java.util.List<String> actualOrder = new java.util.ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                actualOrder.add(child.getLocalName());
            }
        }

        assertEquals("Should have 3 elements", 3, actualOrder.size());
        assertEquals("First element should be 'a' (inserted)", "a", actualOrder.get(0));
        assertEquals("Second element should be 'b'", "b", actualOrder.get(1));
        assertEquals("Third element should be 'c'", "c", actualOrder.get(2));
    }

}