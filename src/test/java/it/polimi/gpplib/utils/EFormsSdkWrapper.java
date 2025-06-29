package it.polimi.gpplib.utils;

import java.util.List;
import org.w3c.dom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for reading and parsing eForms SDK schema files.
 * This class provides utilities for extracting information from XSD schema
 * files
 * that are part of the eForms SDK.
 */
public class EFormsSdkWrapper {

    private static final Logger logger = LoggerFactory.getLogger(EFormsSdkWrapper.class);

    // Path to the UBL Common Aggregate Components XSD file in the eForms SDK
    private static final String UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH = "eForms-SDK/v1.13/schemas/common/UBL-CommonAggregateComponents-2.3.xsd";

    /**
     * Reads the UBL Common Aggregate Components XSD file and extracts the sequence
     * ref fields from the ProcurementProjectType complex type.
     * 
     * @return List of ref attribute values from the ProcurementProjectType sequence
     *         elements
     * @throws XmlUtils.XmlUtilsException if there's an error reading or parsing the
     *                                    XSD file
     */
    public static List<String> getProcurementProjectTypeSchema() {
        logger.debug("Reading ProcurementProjectType schema sequence refs from XSD file");

        try {
            // Load the XSD document from resources
            Document xsdDocument = XmlUtils.loadDocumentFromResource(UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH);

            // Extract the ref elements from the ProcurementProjectType sequence
            List<String> refElements = XmlUtils.extractSequenceRefElements(xsdDocument, "ProcurementProjectType");

            logger.info("Successfully extracted {} ref elements from ProcurementProjectType", refElements.size());
            if (logger.isDebugEnabled()) {
                logger.debug("ProcurementProjectType ref elements: {}", refElements);
            }

            return refElements;

        } catch (Exception e) {
            logger.error("Failed to read ProcurementProjectType sequence refs", e);
            throw new XmlUtils.XmlUtilsException("Failed to read ProcurementProjectType sequence refs", e);
        }
    }
}
