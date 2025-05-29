package it.polimi.uritrejo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import java.io.StringReader;
import java.io.StringWriter;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class App {
    // Make the factory a static object
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static {
        factory.setNamespaceAware(true);
    }

    private static XPathFactory xPathFactory = XPathFactory.newInstance();

    private static final NamespaceContext namespaceCtx = new NamespaceContext() {
        @Override
        public String getNamespaceURI(String prefix) {
            switch (prefix) {
                case "default":
                    return "urn:oasis:names:specification:ubl:schema:xsd:ContractNotice-2";
                case "cac":
                    return "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
                case "cbc":
                    return "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
                case "efac":
                    return "http://data.europa.eu/p27/eforms-ubl-extension-aggregate-components/1";
                case "efbc":
                    return "http://data.europa.eu/p27/eforms-ubl-extension-basic-components/1";
                case "efext":
                    return "http://data.europa.eu/p27/eforms-ubl-extensions/1";
                case "ext":
                    return "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
                case "xsd":
                    return "http://www.w3.org/2001/XMLSchema";
                case "xsi":
                    return "http://www.w3.org/2001/XMLSchema-instance";
                default:
                    return "";
            }
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return null;
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return null;
        }
    };

    public static void main(String[] args) throws Exception {
        // System.out.println("Hello World!");

        // loadNoticeAndFindLots();

        // loadProcurementProject();

        // debugNoticeRoot();

        loadAndInsertNodeInNotice();
    }

    // printDoc prints the XML document in a pretty format
    public static void printDoc(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            System.out.println(writer.toString());
        } catch (Exception e) {
            System.err.println("Failed to print XML document.");
            e.printStackTrace();
        }
    }

    // docFromString converts an XML string to a Document object
    public static Document docFromString(String xmlString) throws Exception {
        // Create a DocumentBuilder to parse the XML string
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

    // docFromFile reads an XML document from a file in the resources folder
    public static Document docFromFile(String resourcePath) throws Exception {
        try (InputStream is = App.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(is);
        }
    }

    // createAndPrintXML creates a simple XML document and prints it
    public static void createAndPrintXML() throws Exception {
        String xmlString = "<root><A>aaa</A></root>";

        Document doc = docFromString(xmlString);
        printDoc(doc);
    }

    // addSimpleElement creates a doc and adds a new element <B>bbb</B> to the XML
    // document
    public static Document addSimpleElement() throws Exception {
        String xmlString = "<root><A>aaa</A></root>";

        Document doc = docFromString(xmlString);

        // create new element <B>bbb</B>
        org.w3c.dom.Element bElement = doc.createElement("B");
        bElement.setTextContent("bbb");
        bElement.setAttribute("listName", "a-letter");
        doc.getDocumentElement().appendChild(bElement);

        // Print updated XML
        printDoc(doc);

        return doc;
    }

    // findBElementText uses XPath to find <B listName="a-letter"> and returns its
    // text content (e.g. "/root/B[@listName='a-letter']")
    public static String findBElementText(Document doc, String path) throws Exception {
        XPath xpath = xPathFactory.newXPath();
        Node bNode = (Node) xpath.evaluate(path, doc, XPathConstants.NODE);
        if (bNode != null) {
            return bNode.getTextContent();
        } else {
            return null;
        }
    }

    // addGElementToRoot parses <G><H></H></G> and appends it to the root element of
    // the given doc
    public static void addGElementToRoot(Document doc) throws Exception {
        String gString = "<G><H></H></G>";
        Document gDoc = docFromString(gString);

        // Import the <G> node from gDoc into doc
        org.w3c.dom.Node gNode = doc.importNode(gDoc.getDocumentElement(), true);

        // Append <G> to root
        doc.getDocumentElement().appendChild(gNode);

        // Print updated XML
        printDoc(doc);
    }

    // writeDocToFile writes the XML Document to a file in
    // src/main/resources/output/
    public static void writeDocToFile(Document doc, String filename) {
        try {
            // Path to the output directory in resources
            String outputDir = Paths.get("src", "main", "resources", "output").toString();
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File outFile = new File(dir, filename);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                transformer.transform(new DOMSource(doc), new StreamResult(fos));
            }
            System.out.println("XML written to: " + outFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to write XML document to file.");
            e.printStackTrace();
        }
    }

    // loadNoticeTest loads a notice, and writes it to a file
    public static void loadNoticeTest() throws Exception {
        String noticePath = "examples/subco_81.xml";
        Document noticeDoc = docFromFile(noticePath);
        printDoc(noticeDoc);
        writeDocToFile(noticeDoc, "patched_notice.xml");
    }

    // findLots finds all <cac:ProcurementProjectLot> with <cbc:ID schemeName="Lot"
    // and text="LOT-0001">
    public static NodeList findLots(Document doc) throws Exception {
        XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext(namespaceCtx);

        String lotPath = "cac:ProcurementProjectLot[cbc:ID/@schemeName='Lot']";
        NodeList lots = (NodeList) xpath.evaluate(lotPath, doc.getDocumentElement(), XPathConstants.NODESET);
        return lots;
    }

    // Example usage after loading the notice
    public static void loadNoticeAndFindLots() throws Exception {
        String noticePath = "examples/subco_81.xml";
        Document noticeDoc = docFromFile(noticePath);

        NodeList lots = findLots(noticeDoc);
        System.out.println("Found lots: " + lots.getLength());
        for (int i = 0; i < lots.getLength(); i++) {
            Node lot = lots.item(i);
            System.out.println("Lot " + (i + 1) + ": " + lot.getNodeName());

            // TODO: move the xpath to a common place lol
            XPath xpath = xPathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            Node idNode = (Node) xpath.evaluate("cbc:ID[@schemeName='Lot']", lot, XPathConstants.NODE);
            if (idNode != null) {
                System.out.println("  Lot ID: " + idNode.getTextContent());
            }
        }
    }

    public static void loadProcurementProject() throws Exception {
        String noticePath = "examples/dummy.xml";
        Document noticeDoc = docFromFile(noticePath);

        XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext(namespaceCtx);

        // String lotPath = "cac:ProcurementProjectLot[cbc:ID/@schemeName='Lot']";
        String procurementProjectPath = "cac:ProcurementProject";

        Node procurementProjectNode = (Node) xpath.evaluate(procurementProjectPath, noticeDoc.getDocumentElement(),
                XPathConstants.NODE);
        if (procurementProjectNode != null) {
            System.out.println("Found ProcurementProject node: " + procurementProjectNode.getNodeName());
        } else {
            System.out.println("ProcurementProject node not found.");
        }

        // NodeList lots = (NodeList) xpath.evaluate(procurementProjectPath,
        // noticeDoc.getDocumentElement(),
        // XPathConstants.NODESET);

        // System.out.println("New found lots: " + lots.getLength());
    }

    public static void debugNoticeRoot() throws Exception {
        String noticePath = "examples/subco_81.xml";
        Document noticeDoc = docFromFile(noticePath);

        // Print the root element name and namespace
        String rootName = noticeDoc.getDocumentElement().getNodeName();
        String rootNS = noticeDoc.getDocumentElement().getNamespaceURI();
        System.out.println("Root element: " + rootName + ", namespace: " + rootNS);
    }

    public static void loadAndInsertNodeInNotice() throws Exception {
        String noticePath = "examples/subco_81.xml";
        Document noticeDoc = docFromFile(noticePath);

        // ??++ AQUIII TODO: copy the GPP stuff in here
        // Create a new element <G><H></H></G>
        // String gString = "<G><H></H></G>";
        String gppCriteriaStr = "<cac:ProcurementAdditionalType xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" "
                +
                "xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "    <!-- Green Public Procurement Criteria (BT-805-Lot) -->" +
                "    <cbc:ProcurementTypeCode listName=\"gpp-criteria\">eu</cbc:ProcurementTypeCode>" +
                "</cac:ProcurementAdditionalType>";
        Document gppCriteriaDoc = docFromString(gppCriteriaStr);
        org.w3c.dom.Node gNode = noticeDoc.importNode(gppCriteriaDoc.getDocumentElement(), true);

        // Append <G> to the root of the notice document
        noticeDoc.getDocumentElement().appendChild(gNode);

        writeDocToFile(noticeDoc, "with_gpp_criteria.xml");
    }

}
