package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GppPatchApplier {

    private static final Logger logger = LoggerFactory.getLogger(GppPatchApplier.class);

    private final EFormsSdkWrapper eFormsSdkWrapper;

    /**
     * Creates a new GppPatchApplier with the specified EFormsSdkWrapper.
     * 
     * @param eFormsSdkWrapper the EFormsSdkWrapper to use for schema validation and
     *                         processing
     */
    public GppPatchApplier(EFormsSdkWrapper eFormsSdkWrapper) {
        this.eFormsSdkWrapper = eFormsSdkWrapper;
    }

    public Notice applyPatch(Notice notice, SuggestedGppPatch patch) {
        if (notice == null || patch == null) {
            logger.error("Notice and patch must not be null");
            throw new IllegalArgumentException("Notice and patch must not be null");
        }

        logger.debug("Applying patch '{}' to lot '{}' at path '{}'", patch.getName(), patch.getLotId(),
                patch.getPath());

        // Validate operation
        if (patch.getOp() == null) {
            logger.error("Patch operation is null");
            throw new IllegalArgumentException("Patch operation cannot be null");
        }

        // Get the lot node
        Node lot = notice.getLotNode(patch.getLotId());
        if (lot == null) {
            logger.error("Lot not found for id: {}", patch.getLotId());
            throw new IllegalArgumentException("Lot not found for id: " + patch.getLotId());
        }

        // Handle different operations
        if (patch.getOp().equalsIgnoreCase(Constants.OP_CREATE)) {
            handleCreateOperation(patch, lot);
        } else if (patch.getOp().equalsIgnoreCase(Constants.OP_REMOVE)) {
            handleRemoveOperation(patch, lot);
        } else {
            logger.error("Unsupported patch operation: {}", patch.getOp());
            throw new IllegalArgumentException("Unsupported patch operation: " + patch.getOp());
        }

        logger.debug("Successfully applied patch '{}' to lot '{}'", patch.getName(), patch.getLotId());
        return notice;
    }

    /**
     * Handles create operations by inserting new nodes into the lot.
     */
    private void handleCreateOperation(SuggestedGppPatch patch, Node lot) {
        logger.debug("Handling create operation for patch '{}'", patch.getName());

        Node insertionNode = XmlUtils.getNodeAtPath(lot, patch.getPath());
        if (insertionNode == null) {
            logger.error("Invalid patch path '{}' for lot '{}'", patch.getPath(), patch.getLotId());
            throw new IllegalArgumentException("Invalid patch path: " + patch.getPath());
        }

        Document valueDoc;
        try {
            valueDoc = XmlUtils.loadDocument(patch.getValue());
        } catch (Exception e) {
            logger.error("Failed to parse patch value as XML: {}", patch.getValue(), e);
            throw new IllegalArgumentException("Invalid patch value: " + patch.getValue(), e);
        }
        if (valueDoc == null) {
            logger.error("Patch value resulted in null document: {}", patch.getValue());
            throw new IllegalArgumentException("Invalid patch value: " + patch.getValue());
        }

        // TODO: if more complex patches are needed, we can extend this logic to cover
        // other paths / parents
        if (patch.getPath().equals(Constants.PATH_PROCUREMENT_PROJECT)) {
            // Extract the root element name from the patch value
            String patchElementName = valueDoc.getDocumentElement().getNodeName();
            logger.debug("Patch element name: {}", patchElementName);

            // Get the full schema list
            List<String> fullSchema = eFormsSdkWrapper.getProcurementProjectTypeSchema();

            // Find the index of our patch element in the schema
            int patchElementIndex = fullSchema.indexOf(patchElementName);

            if (patchElementIndex >= 0 && patchElementIndex < fullSchema.size() - 1) {
                // Get only the elements that come after our patch element
                List<String> elementsAfter = fullSchema.subList(patchElementIndex + 1, fullSchema.size());
                logger.debug("Elements to insert before: {}", elementsAfter);
                XmlUtils.insertIntoNodeBefore(insertionNode, valueDoc.getDocumentElement(), elementsAfter);
            } else {
                // If not found in schema or is the last element, append at the end
                logger.debug("Patch element '{}' not found in schema or is last element, appending at end",
                        patchElementName);
                XmlUtils.insertIntoNode(insertionNode, valueDoc.getDocumentElement());
            }

        } else {
            XmlUtils.insertIntoNode(insertionNode, valueDoc.getDocumentElement());
        }
    }

    /**
     * Handles remove operations by removing nodes from the lot.
     */
    private void handleRemoveOperation(SuggestedGppPatch patch, Node lot) {
        logger.debug("Handling remove operation for patch '{}'", patch.getName());

        boolean removed = XmlUtils.removeNodeAtPath(lot, patch.getPath());
        if (!removed) {
            logger.warn("No node was removed at path '{}' for lot '{}'", patch.getPath(), patch.getLotId());
        }
    }

}
