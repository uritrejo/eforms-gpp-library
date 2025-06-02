package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppPatch;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class GppPatchApplier {

    public GppPatchApplier() {
    }

    public Notice applyPatch(Notice notice, SuggestedGppPatch patch) {
        if (notice == null || patch == null) {
            throw new IllegalArgumentException("Notice and patch must not be null");
        }

        if (patch.getOp() == null || !patch.getOp().equalsIgnoreCase(Constants.OP_CREATE)) {
            System.err.println("Processing invalid patch operation: " + patch.getOp());
            return notice;
        }

        // for now we assume that every patch is a create operation at a specific path
        // in a lot
        Node lot = notice.getLotNode(patch.getLotId());
        if (lot == null) {
            System.err.println("Lot not found for id: " + patch.getLotId());
            return notice;
        }

        Node insertionNode = XmlUtils.getNodeAtPath(lot, patch.getPath());
        if (insertionNode == null) {
            System.err.println("Insertion node not found at path: " + patch.getPath());
            return notice;
        }

        Document valueDoc = XmlUtils.loadDocument(patch.getValue());
        if (valueDoc == null) {
            System.err.println("Failed to parse value document from patch: " + patch.getValue());
            return notice;
        }

        XmlUtils.insertIntoNode(insertionNode, valueDoc.getDocumentElement());

        return notice;
    }

}
