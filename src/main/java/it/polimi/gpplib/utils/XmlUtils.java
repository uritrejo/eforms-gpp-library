package it.polimi.gpplib.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.InputSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import it.polimi.gpplib.model.Constants;

import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);
    private static final XPathFactory xpathFactory = XPathFactory.newInstance();
    private static final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    static {
        docFactory.setNamespaceAware(true);
    }

    protected static final NamespaceContext namespaceCtx = new NamespaceContext() {
        @Override
        public String getNamespaceURI(String prefix) {
            String uri = Constants.NAMESPACE_MAP.get(prefix);
            return uri != null ? uri : "";
        }

        @Override
        // optional
        public String getPrefix(String namespaceURI) {
            for (var entry : Constants.NAMESPACE_MAP.entrySet()) {
                if (entry.getValue().equals(namespaceURI)) {
                    return entry.getKey();
                }
            }
            return null;
        }

        @Override
        // optional
        public Iterator<String> getPrefixes(String namespaceURI) {
            List<String> prefixes = new ArrayList<>();
            for (var entry : Constants.NAMESPACE_MAP.entrySet()) {
                if (entry.getValue().equals(namespaceURI)) {
                    prefixes.add(entry.getKey());
                }
            }
            return prefixes.iterator();
        }
    };

    public static class XmlUtilsException extends RuntimeException {
        public XmlUtilsException(String message, Throwable cause) {
            super(message, cause);
        }

        public XmlUtilsException(String message) {
            super(message);
        }
    }

    /**
     * Returns the content of the XML file as a String.
     * Used mostly for testing purposes.
     */
    public static String getAsXmlString(String filePath) {
        try (java.io.InputStream is = Utils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new XmlUtilsException("Resource not found: " + filePath);
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            throw new XmlUtilsException("Failed to load XML from resource: " + filePath, e);
        }
    }

    /**
     * Loads an XML string into a Document object.
     * This is useful for parsing XML content from strings.
     */
    public static Document loadDocument(String xmlString) {
        logger.debug("Loading XML document from string (length: {} characters)",
                xmlString != null ? xmlString.length() : 0);
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            logger.debug("Successfully loaded XML document");
            return document;
        } catch (Exception e) {
            logger.error("Failed to parse XML string", e);
            throw new XmlUtilsException("Failed to parse XML string", e);
        }
    }

    /**
     * Loads an XML/XSD file from the classpath resources and returns it as a
     * Document.
     * This is useful for loading schema files and other XML resources.
     */
    public static Document loadDocumentFromResource(String resourcePath) {
        logger.debug("Loading XML document from resource: {}", resourcePath);
        try (java.io.InputStream is = XmlUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new XmlUtilsException("Resource not found: " + resourcePath);
            }
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(is);
            logger.debug("Successfully loaded XML document from resource: {}", resourcePath);
            return document;
        } catch (Exception e) {
            logger.error("Failed to load XML from resource: {}", resourcePath, e);
            throw new XmlUtilsException("Failed to load XML from resource: " + resourcePath, e);
        }
    }

    /**
     * Returns a Node at the specified XPath from the root node.
     * If no node is found, it logs and throws an exception.
     */
    public static Node getNodeAtPath(Node root, String path) {
        logger.debug("Evaluating XPath: {} from root node", path);
        try {
            javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            Node result = (Node) xpath.evaluate(path, root, javax.xml.xpath.XPathConstants.NODE);
            if (result != null) {
                logger.debug("Found node at XPath: {}", path);
            } else {
                logger.debug("No node found at XPath: {}", path);
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to evaluate XPath: {}", path, e);
            throw new XmlUtilsException("Failed to evaluate XPath: " + path, e);
        }
    }

    /**
     * Returns the text content of a Node at the specified XPath from the root node.
     * If no node is found, it logs an error and throws an exception.
     */
    public static String getNodeValueAtPath(Node root, String path) {
        Node node = getNodeAtPath(root, path);
        if (node != null) {
            return node.getTextContent().trim();
        } else {
            throw new XmlUtilsException("Node not found at path: " + path);
        }
    }

    /**
     * Returns a NodeList at the specified XPath from the root node.
     * If no nodes are found, it logs an error and returns null.
     */
    public static NodeList getNodesAtPath(Node root, String path) {
        try {
            javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            return (NodeList) xpath.evaluate(path, root, javax.xml.xpath.XPathConstants.NODESET);
        } catch (Exception e) {
            throw new XmlUtilsException("Failed to evaluate XPath: " + path, e);
        }
    }

    /**
     * Checks if a node exists at the specified XPath from the root node.
     * Returns true if the node exists, false otherwise.
     */
    public static boolean doesNodeExistAtPath(Node root, String path) {
        try {
            Node node = getNodeAtPath(root, path);
            return node != null;
        } catch (XmlUtilsException e) {
            return false;
        }
    }

    /**
     * Converts a Document object to an XML string.
     * This is useful for serializing the Document back to XML format.
     */
    public static String docToString(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new XmlUtilsException("Failed to convert Document to XML string", e);
        }
    }

    /**
     * Inserts a new child node into a parent node.
     * The new child node is imported into the parent's document context.
     * If the parent or new child is null, it logs an error and returns.
     */
    public static void insertIntoNode(Node parent, Node newChild) {
        logger.debug("Inserting node '{}' into parent node", newChild != null ? newChild.getNodeName() : "null");
        if (parent == null || newChild == null) {
            logger.error("Cannot insert node: parent or new child node is null");
            throw new XmlUtilsException("Parent or new child node is null");
        }
        try {
            Node importedNode = parent.getOwnerDocument().importNode(newChild, true);
            parent.appendChild(importedNode);
            logger.debug("Successfully inserted node '{}' into parent", newChild.getNodeName());
        } catch (Exception e) {
            logger.error("Failed to insert node '{}' into parent", newChild.getNodeName(), e);
            throw new XmlUtilsException("Failed to insert node into parent", e);
        }
    }

    /**
     * Inserts a new child node into a parent node before the first existing node
     * found in the provided list of XPath expressions.
     * The new child node is imported into the parent's document context.
     * If none of the nodes in the 'before' list exist, the new child is appended at
     * the end.
     * 
     * @param parent   The parent node to insert into
     * @param newChild The new child node to insert
     * @param before   List of XPath expressions representing nodes that should come
     *                 after the new child
     */
    public static void insertIntoNodeBefore(Node parent, Node newChild, List<String> before) {
        logger.debug("Inserting node '{}' into parent node before first existing node from list",
                newChild != null ? newChild.getNodeName() : "null");

        if (parent == null || newChild == null) {
            logger.error("Cannot insert node: parent or new child node is null");
            throw new XmlUtilsException("Parent or new child node is null");
        }

        if (before == null || before.isEmpty()) {
            logger.debug("Before list is null or empty, appending node at the end");
            insertIntoNode(parent, newChild);
            return;
        }

        try {
            // Import the new child into the parent's document context
            Node importedNode = parent.getOwnerDocument().importNode(newChild, true);

            // Look for the first existing node from the 'before' list
            Node referenceNode = null;
            String foundPath = null;

            for (String path : before) {
                logger.debug("Checking if node exists at path: {}", path);
                try {
                    Node candidate = getNodeAtPath(parent, path);
                    if (candidate != null) {
                        // Verify that the candidate is actually a direct child of parent
                        if (candidate.getParentNode() == parent) {
                            referenceNode = candidate;
                            foundPath = path;
                            logger.debug("Found reference node at path: {}", path);
                            break;
                        } else {
                            logger.debug("Node found at path '{}' but is not a direct child of parent", path);
                        }
                    }
                } catch (XmlUtilsException e) {
                    // Continue searching if this path doesn't exist
                    logger.debug("No node found at path: {}", path);
                }
            }

            if (referenceNode != null) {
                // Insert before the found reference node
                parent.insertBefore(importedNode, referenceNode);
                logger.debug("Successfully inserted node '{}' before reference node at path: {}",
                        newChild.getNodeName(), foundPath);
            } else {
                // None of the 'before' nodes exist, append at the end
                parent.appendChild(importedNode);
                logger.debug("No reference nodes found, appended node '{}' at the end", newChild.getNodeName());
            }

        } catch (Exception e) {
            logger.error("Failed to insert node '{}' into parent", newChild.getNodeName(), e);
            throw new XmlUtilsException("Failed to insert node into parent", e);
        }
    }

    /**
     * Removes a node at the specified XPath from the given root node.
     * If the node is found, it will be removed from its parent.
     * If no node is found at the specified path, it logs a warning.
     * 
     * @param root The root node to search from
     * @param path The XPath expression to locate the node to remove
     * @return true if a node was found and removed, false if no node was found
     */
    public static boolean removeNodeAtPath(Node root, String path) {
        logger.debug("Attempting to remove node at XPath: {}", path);
        if (root == null) {
            logger.error("Cannot remove node: root node is null");
            throw new XmlUtilsException("Root node is null");
        }

        try {
            Node nodeToRemove = getNodeAtPath(root, path);
            if (nodeToRemove != null) {
                Node parentNode = nodeToRemove.getParentNode();
                if (parentNode != null) {
                    parentNode.removeChild(nodeToRemove);
                    logger.debug("Successfully removed node '{}' at XPath: {}", nodeToRemove.getNodeName(), path);
                    return true;
                } else {
                    logger.warn("Cannot remove node at XPath '{}': node has no parent", path);
                    return false;
                }
            } else {
                logger.warn("No node found to remove at XPath: {}", path);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to remove node at XPath: {}", path, e);
            throw new XmlUtilsException("Failed to remove node at XPath: " + path, e);
        }
    }

    /**
     * Extracts all ref attributes from xsd:element elements within a specific
     * xsd:complexType's sequence.
     * This is useful for analyzing XSD schema structure.
     * 
     * @param xsdDocument     The XSD document to search in
     * @param complexTypeName The name of the complexType to look for
     * @return List of ref attribute values from elements in the sequence, or empty
     *         list if not found
     */
    public static List<String> extractSequenceRefElements(Document xsdDocument, String complexTypeName) {
        logger.debug("Extracting sequence ref elements from complexType: {}", complexTypeName);
        List<String> refElements = new ArrayList<>();

        try {
            javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);

            // XPath to find the complexType with the specified name and its sequence
            // elements with ref attributes
            String xpathExpression = String.format(
                    "//xsd:complexType[@name='%s']/xsd:sequence/xsd:element[@ref]/@ref",
                    complexTypeName);

            NodeList refNodes = (NodeList) xpath.evaluate(xpathExpression, xsdDocument,
                    javax.xml.xpath.XPathConstants.NODESET);

            for (int i = 0; i < refNodes.getLength(); i++) {
                Node refNode = refNodes.item(i);
                String refValue = refNode.getNodeValue();
                if (refValue != null && !refValue.trim().isEmpty()) {
                    refElements.add(refValue.trim());
                    logger.debug("Found ref element: {}", refValue);
                }
            }

            logger.debug("Extracted {} ref elements from complexType: {}", refElements.size(), complexTypeName);
            return refElements;

        } catch (Exception e) {
            logger.error("Failed to extract sequence ref elements from complexType: {}", complexTypeName, e);
            throw new XmlUtilsException("Failed to extract sequence ref elements from complexType: " + complexTypeName,
                    e);
        }
    }

}
