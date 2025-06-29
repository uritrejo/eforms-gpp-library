package it.polimi.gpplib.model;

import org.w3c.dom.Document;
import it.polimi.gpplib.utils.XmlUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a public procurement notice with methods for extracting and
 * manipulating
 * procurement-related information such as CPV codes, lot details, and tender
 * specifications.
 * 
 * <p>
 * This class wraps an XML document containing a procurement notice and provides
 * convenient methods to access various elements of the notice structure,
 * including:
 * <ul>
 * <li>Procurement project information and CPV codes</li>
 * <li>Lot-specific details and classifications</li>
 * <li>Tender requirements and criteria</li>
 * <li>XML manipulation capabilities for applying patches</li>
 * </ul>
 * 
 * <p>
 * The underlying XML structure follows the eForms standard for public
 * procurement notices.
 * 
 * @author Politecnico di Milano
 * @version 1.0
 * @since 1.0
 */
public class Notice {

    private static final Logger logger = LoggerFactory.getLogger(Notice.class);

    /** The underlying XML document representing the procurement notice. */
    private Document doc;

    /**
     * Creates a new Notice by parsing the provided XML string.
     * 
     * @param xmlString the XML representation of the procurement notice
     * @throws it.polimi.gpplib.utils.XmlUtils.XmlUtilsException if the XML cannot
     *                                                           be parsed
     */
    public Notice(String xmlString) {
        logger.debug("Creating Notice from XML string (length: {} characters)",
                xmlString != null ? xmlString.length() : 0);
        this.doc = XmlUtils.loadDocument(xmlString);
        logger.debug("Notice created successfully from XML");
    }

    /**
     * Converts the notice back to an XML string representation.
     * 
     * @return the XML string representation of this notice
     */
    public String toXmlString() {
        return XmlUtils.docToString(doc);
    }

    /**
     * Provides access to the underlying Document object.
     * This method is mostly intended for patch handlers to access the XML structure
     * directly.
     * 
     * @return the underlying XML Document object
     */
    public Document getDoc() {
        return doc;
    }

    /**
     * Returns the language code of the notice.
     * 
     * <p>
     * The notice language code indicates the language in which the procurement
     * notice is published (e.g., "ENG" for English, "ITA" for Italian, "SPA" for
     * Spanish).
     * 
     * @return the notice language code as a string, or null if not found
     */
    public String getNoticeLanguage() {
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_NOTICE_LANGUAGE);
        return node != null ? node.getTextContent().trim() : null;
    }

    /**
     * Returns the main CPV code of the ProcurementProject.
     * 
     * <p>
     * The Common Procurement Vocabulary (CPV) code identifies the subject matter
     * of the procurement contract. The main CPV code represents the primary
     * category
     * of goods, services, or works being procured.
     * 
     * @return the main CPV code as a string, or null if not found
     */
    public String getProcurementProjectMainCpv() {
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_MAIN_CPV);
        return node != null ? node.getTextContent().trim() : null;
    }

    /**
     * Returns the additional CPV codes of the ProcurementProject.
     * 
     * <p>
     * Additional CPV codes provide supplementary classification for
     * procurement that involves multiple categories or subcategories of
     * goods, services, or works.
     * 
     * @return a list of additional CPV codes, empty if none are found
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
     * 
     * <p>
     * This is a convenience method that combines the main CPV code with all
     * additional CPV codes into a single list. This is useful for comprehensive
     * analysis of all procurement categories covered by the project.
     * 
     * @return a list containing all CPV codes in the order: [main, additional...]
     */
    public List<String> getAllProcurementProjectCpvs() {
        List<String> cpvs = new ArrayList<>();
        String mainCpv = getProcurementProjectMainCpv();
        if (mainCpv != null) {
            cpvs.add(mainCpv);
        }
        cpvs.addAll(getProcurementProjectAdditionalCpvs());
        logger.debug("Found {} total CPV codes for procurement project", cpvs.size());
        return cpvs;
    }

    /**
     * Finds the ProcurementProjectLot node for a given lot ID.
     * 
     * <p>
     * This method searches through all procurement lots in the notice,
     * checks their ID elements, and returns the XML node of the lot that
     * matches the specified lot ID. The comparison is case-insensitive.
     * 
     * @param lotId the ID of the lot to find
     * @return the XML Node representing the lot, or null if not found
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
        logger.debug("Found {} lots in notice", lotIds.size());
        return lotIds;
    }

    public String getEFormsSdkVersion() {
        Node node = XmlUtils.getNodeAtPath(doc.getDocumentElement(), Constants.PATH_EFORMS_SDK_VERSION);
        return node != null ? node.getTextContent().trim() : null;
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
}
