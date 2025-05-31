package it.polimi.gpplib.model;

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
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Notice {
    // TODO: maybe have a file for all the path definitions?
    // TODO: eventually load them directly from the SDK somehow
    // xpaths for the notice
    private static final String MAIN_CPV_PATH = "//*[local-name()='ProcurementProject']/*[local-name()='MainCommodityClassification']/*[local-name()='ItemClassificationCode']";
    private static final String ADDITIONAL_CPVS_PATH = "//*[local-name()='ProcurementProject']/*[local-name()='AdditionalCommodityClassification']/*[local-name()='ItemClassificationCode']";

    // xpaths related to lots
    // ND-Lot
    private static final String LOT_PATH = "cac:ProcurementProjectLot[cbc:ID/@schemeName='Lot']";

    // TODO: at some point you might need to compute the path to the lot from the
    // SDK (to replace the hardcoded path in lots) (just go deep until ND-Lot)

    // Q? ND or BT?
    private static final String ID_PATH_IN_LOT = "cbc:ID[@schemeName='Lot']";
    // ND-LotMainClassification
    private static final String MAIN_CPV_PATH_IN_LOT = "cac:ProcurementProject/cac:MainCommodityClassification";
    // ND-LotAdditionalClassification
    private static final String ADDITIONAL_CPVS_PATH_IN_LOT = "cac:ProcurementProject/cac:AdditionalCommodityClassification";

    private static final XPathFactory xpathFactory = XPathFactory.newInstance();
    private static final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    static {
        docFactory.setNamespaceAware(true);
    }

    // TODO: get this from the SDK or something
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

    private Document doc;

    public Notice(String xmlString) {
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            this.doc = doc;
        } catch (Exception e) {
            System.err.println("Failed to parse XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String toXmlString() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            System.err.println("Failed to convert Document to XML string: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Gives access to the underlying Document object.
     * (Moslty intended for the patch handlers to access the XML structure)
     */
    public Document getDoc() {
        return doc;
    }

    /**
     * Returns the main CPV code of the ProcurementProject.
     */
    public String getProcurementProjectMainCpv() {
        try {
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            Node node = (Node) xpath.evaluate(MAIN_CPV_PATH, doc, XPathConstants.NODE);
            return node != null ? node.getTextContent() : null;
        } catch (Exception e) {
            System.err.println("Failed to get ProcurementProject main CPV: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the additional CPV codes of the ProcurementProject.
     */
    public List<String> getProcurementProjectAdditionalCpvs() {
        List<String> cpvs = new ArrayList<>();
        try {
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            NodeList nodes = (NodeList) xpath.evaluate(ADDITIONAL_CPVS_PATH, doc.getDocumentElement(),
                    XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                cpvs.add(nodes.item(i).getTextContent());
            }
        } catch (Exception e) {
            System.err.println("Failed to get ProcurementProject additional CPVs: " + e.getMessage());
        }
        return cpvs;
    }

    /**
     * Helper: Finds the ProcurementProjectLot node for a given lot ID.
     * This version finds all lots, checks their ID child, and returns the matching
     * lot node.
     */
    private Node getLotNode(String lotId) {
        try {
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            NodeList lots = (NodeList) xpath.evaluate(LOT_PATH, doc.getDocumentElement(), XPathConstants.NODESET);
            for (int i = 0; i < lots.getLength(); i++) {
                Node lot = lots.item(i);
                Node idNode = (Node) xpath.evaluate(ID_PATH_IN_LOT, lot, XPathConstants.NODE);
                if (idNode != null && lotId.equals(idNode.getTextContent())) {
                    return lot;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to find lot node for lotId " + lotId + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns the main CPV code for the given lot ID.
     */
    public String getLotMainCpv(String lotId) {
        Node lotNode = getLotNode(lotId);
        if (lotNode == null)
            return null;
        try {
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            Node node = (Node) xpath.evaluate(MAIN_CPV_PATH_IN_LOT, lotNode, XPathConstants.NODE);
            return node != null ? node.getTextContent() : null;
        } catch (Exception e) {
            System.err.println("Failed to get main CPV for lot " + lotId + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the additional CPV codes for the given lot ID.
     */
    public List<String> getLotAdditionalCpvs(String lotId) {
        List<String> cpvs = new ArrayList<>();
        Node lotNode = getLotNode(lotId);
        if (lotNode == null)
            return cpvs;
        try {
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceCtx);
            NodeList nodes = (NodeList) xpath.evaluate(ADDITIONAL_CPVS_PATH_IN_LOT, lotNode, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                cpvs.add(nodes.item(i).getTextContent());
            }
        } catch (Exception e) {
            System.err.println("Failed to get additional CPVs for lot " + lotId + ": " + e.getMessage());
        }
        return cpvs;
    }

    @Override
    public String toString() {
        // TODO: should include a lot more later
        return "Notice{" +
                "mainCpv='" + getProcurementProjectMainCpv() + '\'' +
                ", additionalCpvs=" + getProcurementProjectAdditionalCpvs() +
                '}';
    }

    // TODO: add getters and setters, not exactly patch handlers...
    // Q: for what fields?

    // TODO: see gpp-tool notebook "Path definitions" to see more fields that would
    // be useful for getters

    public static void main(String[] args) {
        // Example XML string (minimal, adjust as needed for real testing)
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ContractNotice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:ContractNotice-2\" " +
                "xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" " +
                "xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">\n" +
                "  <cac:ProcurementProject>\n" +
                "    <cac:MainCommodityClassification>\n" +
                "      <cbc:ItemClassificationCode>12345678</cbc:ItemClassificationCode>\n" +
                "    </cac:MainCommodityClassification>\n" +
                "    <cac:AdditionalCommodityClassification>\n" +
                "      <cbc:ItemClassificationCode>87654321</cbc:ItemClassificationCode>\n" +
                "    </cac:AdditionalCommodityClassification>\n" +
                "  </cac:ProcurementProject>\n" +
                "  <cac:ProcurementProjectLot>\n" +
                "    <cbc:ID schemeName=\"Lot\">LOT1</cbc:ID>\n" +
                "    <cac:ProcurementProject>\n" +
                "      <cac:MainCommodityClassification>\n" +
                "        <cbc:ItemClassificationCode>11111111</cbc:ItemClassificationCode>\n" +
                "      </cac:MainCommodityClassification>\n" +
                "      <cac:AdditionalCommodityClassification>\n" +
                "        <cbc:ItemClassificationCode>22222222</cbc:ItemClassificationCode>\n" +
                "      </cac:AdditionalCommodityClassification>\n" +
                "    </cac:ProcurementProject>\n" +
                "  </cac:ProcurementProjectLot>\n" +
                "</ContractNotice>";

        Notice notice = new Notice(xml);

        System.out.println("Main CPV: " + notice.getProcurementProjectMainCpv());
        System.out.println("Additional CPVs: " + notice.getProcurementProjectAdditionalCpvs());

        String lotId = "LOT1";
        System.out.println("Lot " + lotId + " Main CPV: " + notice.getLotMainCpv(lotId));
        System.out.println("Lot " + lotId + " Additional CPVs: " + notice.getLotAdditionalCpvs(lotId));
    }
}
