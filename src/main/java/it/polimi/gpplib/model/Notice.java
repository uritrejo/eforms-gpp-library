package it.polimi.gpplib.model;

import org.w3c.dom.Document;
import it.polimi.gpplib.utils.XmlUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

public class Notice {

    private Document doc;

    public Notice(String xmlString) {
        this.doc = XmlUtils.loadDocument(xmlString);
    }

    public String toXmlString() {
        return XmlUtils.docToString(doc);
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
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_MAIN_CPV);
        return node != null ? node.getTextContent().trim() : null;
    }

    /**
     * Returns the additional CPV codes of the ProcurementProject.
     */
    public List<String> getProcurementProjectAdditionalCpvs() {
        List<String> cpvs = new ArrayList<>();
        NodeList nodes = XmlUtils.getNodesAtPath(doc.getDocumentElement(), Constants.PATH_ADDITIONAL_CPVS);
        for (int i = 0; i < nodes.getLength(); i++) {
            cpvs.add(nodes.item(i).getTextContent().trim());
        }
        return cpvs;
    }

    /**
     * Returns a list of all CPV codes (main and additional) for the
     * ProcurementProject.
     * The returned list is in the order: [main, additional...].
     */
    public List<String> getAllProcurementProjectCpvs() {
        List<String> cpvs = new ArrayList<>();
        String mainCpv = getProcurementProjectMainCpv();
        if (mainCpv != null) {
            cpvs.add(mainCpv);
        }
        cpvs.addAll(getProcurementProjectAdditionalCpvs());
        return cpvs;
    }

    /**
     * Finds the ProcurementProjectLot node for a given lot ID.
     * This version finds all lots, checks their ID child, and returns the matching
     * lot node.
     */
    public Node getLotNode(String lotId) {
        NodeList lots = XmlUtils.getNodesAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        for (int i = 0; i < lots.getLength(); i++) {
            Node lot = lots.item(i);
            String id = XmlUtils.getNodeValueAtPath(lot, Constants.PATH_IN_LOT_ID);
            if (id != null && lotId.equalsIgnoreCase(id)) {
                return lot;
            }
        }

        return null;
    }

    public boolean doesPathExistInLot(String lotId, String path) {
        Node lotNode = getLotNode(lotId);
        if (lotNode == null) {
            return false;
        }

        return XmlUtils.doesNodeExistAtPath(lotNode, path);
    }

    /**
     * Returns the list of lot IDs present in the notice.
     * 
     * @return
     */
    public List<String> getLotIds() {
        List<String> lotIds = new ArrayList<>();
        NodeList lots = XmlUtils.getNodesAtPath(doc.getDocumentElement(), Constants.PATH_LOT);
        for (int i = 0; i < lots.getLength(); i++) {
            Node lot = lots.item(i);
            String id = XmlUtils.getNodeValueAtPath(lot, Constants.PATH_IN_LOT_ID);
            if (id != null && !id.isEmpty()) {
                lotIds.add(id);
            }
        }

        return lotIds;
    }

    /**
     * Returns the main CPV code for the given lot ID.
     */
    public String getLotMainCpv(String lotId) {
        Node lotNode = getLotNode(lotId);
        if (lotNode == null)
            return null;

        return XmlUtils.getNodeValueAtPath(lotNode, Constants.PATH_IN_LOT_MAIN_CPV);
    }

    /**
     * Returns the additional CPV codes for the given lot ID.
     */
    public List<String> getLotAdditionalCpvs(String lotId) {
        List<String> cpvs = new ArrayList<>();
        Node lotNode = getLotNode(lotId);
        if (lotNode == null)
            return cpvs;

        NodeList nodes = XmlUtils.getNodesAtPath(lotNode, Constants.PATH_IN_LOT_ADDITIONAL_CPVS);
        for (int i = 0; i < nodes.getLength(); i++) {
            cpvs.add(nodes.item(i).getTextContent().trim());
        }

        return cpvs;
    }

    /**
     * Returns a list of all CPV codes (main and additional) for the given lot.
     * The returned list is in the order: [main, additional...].
     * If the lot does not exist, returns an empty list.
     */
    public List<String> getAllLotCpvs(String lotId) {
        List<String> cpvs = new ArrayList<>();
        String mainCpv = getLotMainCpv(lotId);
        if (mainCpv != null) {
            cpvs.add(mainCpv);
        }
        cpvs.addAll(getLotAdditionalCpvs(lotId));
        return cpvs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notice{");
        sb.append("mainCpv='").append(getProcurementProjectMainCpv()).append('\'');
        sb.append(", additionalCpvs=").append(getProcurementProjectAdditionalCpvs());

        List<String> lotIds = getLotIds();
        sb.append(", lots=[");
        for (int i = 0; i < lotIds.size(); i++) {
            String lotId = lotIds.get(i);
            sb.append("{id='").append(lotId).append('\'');
            sb.append(", mainCpv='").append(getLotMainCpv(lotId)).append('\'');
            sb.append(", additionalCpvs=").append(getLotAdditionalCpvs(lotId));
            sb.append('}');
            if (i < lotIds.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        sb.append('}');
        return sb.toString();
    }

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
        System.out.println("Notice XML:" + notice);
    }
}
