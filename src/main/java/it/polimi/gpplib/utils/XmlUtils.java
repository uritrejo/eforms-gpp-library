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

public class XmlUtils {
    private static final XPathFactory xpathFactory = XPathFactory.newInstance();
    private static final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    static {
        docFactory.setNamespaceAware(true);
    }

    private static final NamespaceContext namespaceCtx = new NamespaceContext() {
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
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            throw new XmlUtilsException("Failed to parse XML string", e);
        }
    }

    /**
     * Returns a Node at the specified XPath from the root node.
     * If no node is found, it logs and throws an exception.
     */
    public static Node getNodeAtPath(Node root, String path) {
        try {
            javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            return (Node) xpath.evaluate(path, root, javax.xml.xpath.XPathConstants.NODE);
        } catch (Exception e) {
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
        if (parent == null || newChild == null) {
            throw new XmlUtilsException("Parent or new child node is null");
        }
        try {
            Node importedNode = parent.getOwnerDocument().importNode(newChild, true);
            parent.appendChild(importedNode);
        } catch (Exception e) {
            throw new XmlUtilsException("Failed to insert node into parent", e);
        }
    }

}
