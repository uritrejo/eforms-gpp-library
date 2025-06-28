package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppPatch;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GppPatchApplier {

    private static final Logger logger = LoggerFactory.getLogger(GppPatchApplier.class);

    public GppPatchApplier() {
    }

    public Notice applyPatch(Notice notice, SuggestedGppPatch patch) {
        logger.debug("Applying patch '{}' to lot '{}' at path '{}'", patch.getName(), patch.getLotId(),
                patch.getPath());

        if (notice == null || patch == null) {
            logger.error("Notice and patch must not be null");
            throw new IllegalArgumentException("Notice and patch must not be null");
        }

        if (patch.getOp() == null || !patch.getOp().equalsIgnoreCase(Constants.OP_CREATE)) {
            logger.error("Invalid patch operation: {}", patch.getOp());
            throw new IllegalArgumentException("Invalid patch operation: " + patch.getOp());
        }

        // for now we assume that every patch is a create operation at a specific path
        // in a lot
        Node lot = notice.getLotNode(patch.getLotId());
        if (lot == null) {
            logger.error("Lot not found for id: {}", patch.getLotId());
            throw new IllegalArgumentException("Lot not found for id: " + patch.getLotId());
        }

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

        XmlUtils.insertIntoNode(insertionNode, valueDoc.getDocumentElement());
        logger.debug("Successfully applied patch '{}' to lot '{}'", patch.getName(), patch.getLotId());

        return notice;
    }

}
