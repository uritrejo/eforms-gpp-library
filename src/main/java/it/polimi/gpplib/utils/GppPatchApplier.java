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
            throw new IllegalArgumentException("Invalid patch operation: " + patch.getOp());
        }

        // for now we assume that every patch is a create operation at a specific path
        // in a lot
        Node lot = notice.getLotNode(patch.getLotId());
        if (lot == null) {
            throw new IllegalArgumentException("Lot not found for id: " + patch.getLotId());
        }

        Node insertionNode = XmlUtils.getNodeAtPath(lot, patch.getPath());
        if (insertionNode == null) {
            throw new IllegalArgumentException("Invalid patch path: " + patch.getPath());
        }

        Document valueDoc;
        try {
            valueDoc = XmlUtils.loadDocument(patch.getValue());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid patch value: " + patch.getValue(), e);
        }
        if (valueDoc == null) {
            throw new IllegalArgumentException("Invalid patch value: " + patch.getValue());
        }

        XmlUtils.insertIntoNode(insertionNode, valueDoc.getDocumentElement());

        return notice;
    }

}
