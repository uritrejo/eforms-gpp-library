package it.polimi.gpplib.utils;

import java.util.List;
import org.w3c.dom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.polimi.gpplib.model.Constants;

/**
 * Wrapper class for reading and parsing eForms SDK schema files.
 * This class provides utilities for extracting information from XSD schema
 * files
 * that are part of the eForms SDK.
 * 
 * The class loads the schema information on construction and provides getter
 * methods
 * to access the parsed data.
 */
public class EFormsSdkWrapper {

    private static final Logger logger = LoggerFactory.getLogger(EFormsSdkWrapper.class);

    private final List<String> procurementProjectTypeSchema;

    /**
     * Constructor that uses the default eForms SDK path from Constants.
     * Loads the UBL Common Aggregate Components XSD file and extracts the
     * ProcurementProjectType schema on construction.
     * 
     * @throws XmlUtils.XmlUtilsException if there's an error reading or parsing the
     *                                    XSD file
     */
    public EFormsSdkWrapper() {
        this(Constants.EFORMS_SDK_UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH);
    }

    /**
     * Constructor that takes a custom path to the XSD file.
     * Loads the specified XSD file and extracts the ProcurementProjectType schema
     * on construction.
     * 
     * @param xsdFilePath Path to the UBL Common Aggregate Components XSD file
     * @throws XmlUtils.XmlUtilsException if there's an error reading or parsing the
     *                                    XSD file
     */
    public EFormsSdkWrapper(String xsdFilePath) {
        logger.debug("Initializing EFormsSdkWrapper with XSD file: {}", xsdFilePath);
        this.procurementProjectTypeSchema = loadProcurementProjectTypeSchema(xsdFilePath);
        logger.info("EFormsSdkWrapper initialized successfully with {} schema elements",
                this.procurementProjectTypeSchema.size());
    }

    /**
     * Gets the ProcurementProjectType schema elements.
     * Returns the list of ref attribute values from the ProcurementProjectType
     * sequence
     * that was loaded during construction.
     * 
     * @return List of ref attribute values from the ProcurementProjectType sequence
     *         elements
     */
    public List<String> getProcurementProjectTypeSchema() {
        return this.procurementProjectTypeSchema;
    }

    /**
     * Private method to load and parse the ProcurementProjectType schema from the
     * XSD file.
     * 
     * @param xsdFilePath Path to the XSD file
     * @return List of ref attribute values from the ProcurementProjectType sequence
     *         elements
     * @throws XmlUtils.XmlUtilsException if there's an error reading or parsing the
     *                                    XSD file
     */
    private List<String> loadProcurementProjectTypeSchema(String xsdFilePath) {
        logger.debug("Loading ProcurementProjectType schema from XSD file: {}", xsdFilePath);

        try {
            // Load the XSD document from resources
            Document xsdDocument = XmlUtils.loadDocumentFromResource(xsdFilePath);

            // Extract the ref elements from the ProcurementProjectType sequence
            List<String> refElements = XmlUtils.extractSequenceRefElements(xsdDocument,
                    Constants.TYPE_NAME_PROCUREMENT_PROJECT);

            logger.info("Successfully extracted {} ref elements from ProcurementProjectType", refElements.size());
            if (logger.isDebugEnabled()) {
                logger.debug("ProcurementProjectType ref elements: {}", refElements);
            }

            return refElements;

        } catch (Exception e) {
            logger.error("Failed to load ProcurementProjectType schema from: {}", xsdFilePath, e);
            throw new XmlUtils.XmlUtilsException("Failed to load ProcurementProjectType schema from: " + xsdFilePath,
                    e);
        }
    }
}
