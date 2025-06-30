package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppPatch;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
        } else if (patch.getOp().equalsIgnoreCase(Constants.OP_UPDATE)) {
            handleUpdateOperation(patch, lot);
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

    /**
     * Handles update operations by replacing existing nodes in the lot.
     * This method finds the node at the specified path, stores its parent,
     * removes the old node, and then inserts the new value.
     */
    private void handleUpdateOperation(SuggestedGppPatch patch, Node lot) {
        logger.debug("Handling update operation for patch '{}'", patch.getName());

        // Find the node to be updated
        Node nodeToUpdate = XmlUtils.getNodeAtPath(lot, patch.getPath());
        if (nodeToUpdate == null) {
            logger.error("Node not found at path '{}' for lot '{}'", patch.getPath(), patch.getLotId());
            throw new IllegalArgumentException("Node not found at path: " + patch.getPath());
        }

        // Get the parent node before removing the old node
        Node parentNode = nodeToUpdate.getParentNode();
        if (parentNode == null) {
            logger.error("Parent node not found for node at path '{}' for lot '{}'", patch.getPath(), patch.getLotId());
            throw new IllegalArgumentException("Parent node not found for path: " + patch.getPath());
        }

        // Parse the new value
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

        // Store the next sibling to maintain order
        Node nextSibling = nodeToUpdate.getNextSibling();

        // Remove the old node
        parentNode.removeChild(nodeToUpdate);
        logger.debug("Removed old node at path '{}'", patch.getPath());

        // Import and insert the new node
        Node newNode = parentNode.getOwnerDocument().importNode(valueDoc.getDocumentElement(), true);

        if (nextSibling != null) {
            // Insert before the next sibling to maintain order
            parentNode.insertBefore(newNode, nextSibling);
        } else {
            // Append at the end if there's no next sibling
            parentNode.appendChild(newNode);
        }

        logger.debug("Successfully updated node at path '{}' with new value", patch.getPath());
    }

    /**
     * Updates award criteria weights in the notice to ensure proper distribution.
     * 
     * This method processes each lot to update award criteria weights according to
     * these rules:
     * - The total sum of all weights per lot should be exactly 100
     * - Award criteria introduced through patches should sum to
     * DEFAULT_WEIGHT_GPP_CRITERIA
     * - Pre-existing award criteria should sum to (100 -
     * DEFAULT_WEIGHT_GPP_CRITERIA)
     * - Pre-existing weights are distributed proportionally to their original
     * values
     * - Only processes criteria with PLACEHOLDER_WEIGHT values
     * 
     * @param patchedNotice  The notice after patches have been applied
     * @param originalNotice The original notice before any patches were applied
     */
    public void updateAwardCriteriaWeights(Notice patchedNotice, Notice originalNotice) {
        logger.info("Starting award criteria weights update");

        List<String> lotIds = patchedNotice.getLotIds();
        logger.debug("Processing {} lots for weight updates", lotIds.size());

        for (String lotId : lotIds) {
            updateAwardCriteriaWeightsForLot(patchedNotice, originalNotice, lotId);
        }

        logger.info("Completed award criteria weights update");
    }

    /**
     * Updates award criteria weights for a specific lot.
     */
    private void updateAwardCriteriaWeightsForLot(Notice patchedNotice, Notice originalNotice, String lotId) {
        logger.debug("Processing weight updates for lot: {}", lotId);

        Node patchedLot = patchedNotice.getLotNode(lotId);
        Node originalLot = originalNotice.getLotNode(lotId);

        if (patchedLot == null) {
            logger.warn("Lot {} not found in patched notice", lotId);
            return;
        }

        // Get all award criteria nodes in the patched notice
        NodeList patchedCriteriaNodes = XmlUtils.getNodesAtPath(patchedLot, Constants.PATH_AWARD_CRITERION);
        if (patchedCriteriaNodes.getLength() == 0) {
            logger.debug("No award criteria found in lot {}", lotId);
            return;
        }

        // Categorize criteria as new (from patches) or existing (pre-existing)
        List<Node> newCriteria = new ArrayList<>();
        List<Node> existingCriteria = new ArrayList<>();
        Map<Node, Integer> originalWeights = new HashMap<>();

        categorizeAwardCriteria(patchedCriteriaNodes, originalLot, newCriteria, existingCriteria, originalWeights);

        // Only proceed if we have criteria with placeholder weights
        List<Node> criteriaWithPlaceholders = getCriteriaWithPlaceholderWeights(patchedCriteriaNodes);
        if (criteriaWithPlaceholders.isEmpty()) {
            logger.debug("No criteria with placeholder weights found in lot {}", lotId);
            return;
        }

        logger.debug("Found {} new criteria and {} existing criteria with weights to update in lot {}",
                newCriteria.size(), existingCriteria.size(), lotId);

        // Calculate and distribute weights
        distributeWeights(newCriteria, existingCriteria, originalWeights);
    }

    /**
     * Categorizes award criteria nodes into new (from patches) and existing
     * (pre-existing).
     */
    private void categorizeAwardCriteria(NodeList patchedCriteriaNodes, Node originalLot,
            List<Node> newCriteria, List<Node> existingCriteria,
            Map<Node, Integer> originalWeights) {

        // Get original criteria for comparison
        NodeList originalCriteriaNodes = null;
        if (originalLot != null) {
            originalCriteriaNodes = XmlUtils.getNodesAtPath(originalLot, Constants.PATH_AWARD_CRITERION);
        }

        for (int i = 0; i < patchedCriteriaNodes.getLength(); i++) {
            Node patchedCriterion = patchedCriteriaNodes.item(i);

            // Check if this criterion has a placeholder weight, if not skip it
            if (!hasPlaceholderWeight(patchedCriterion)) {
                continue;
            }

            // Try to find matching criterion in original notice
            Node matchingOriginal = findMatchingCriterion(patchedCriterion, originalCriteriaNodes);

            if (matchingOriginal != null) {
                // This is an existing criterion
                existingCriteria.add(patchedCriterion);

                // Try to get the original weight
                Integer originalWeight = getOriginalWeight(matchingOriginal);
                if (originalWeight != null) {
                    originalWeights.put(patchedCriterion, originalWeight);
                }
            } else {
                // This is a new criterion from patches
                newCriteria.add(patchedCriterion);
            }
        }
    }

    /**
     * Checks if a criterion node has a placeholder weight.
     */
    private boolean hasPlaceholderWeight(Node criterionNode) {
        try {
            String weight = XmlUtils.getNodeValueAtPath(criterionNode, Constants.PATH_IN_AWARD_CRITERION_WEIGHT);
            return Constants.PLACEHOLDER_WEIGHT.equals(weight);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets list of criteria that have placeholder weights.
     */
    private List<Node> getCriteriaWithPlaceholderWeights(NodeList criteriaNodes) {
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < criteriaNodes.getLength(); i++) {
            Node criterion = criteriaNodes.item(i);
            if (hasPlaceholderWeight(criterion)) {
                result.add(criterion);
            }
        }
        return result;
    }

    /**
     * Finds a matching criterion in the original criteria list based on name.
     */
    private Node findMatchingCriterion(Node patchedCriterion, NodeList originalCriteria) {
        if (originalCriteria == null) {
            return null;
        }

        try {
            String patchedName = XmlUtils.getNodeValueAtPath(patchedCriterion, Constants.PATH_IN_AWARD_CRITERION_NAME);
            if (patchedName == null || patchedName.trim().isEmpty()) {
                return null;
            }

            for (int i = 0; i < originalCriteria.getLength(); i++) {
                Node originalCriterion = originalCriteria.item(i);
                try {
                    String originalName = XmlUtils.getNodeValueAtPath(originalCriterion,
                            Constants.PATH_IN_AWARD_CRITERION_NAME);
                    if (patchedName.equals(originalName)) {
                        return originalCriterion;
                    }
                } catch (Exception e) {
                    // Continue to next criterion if name can't be retrieved
                    continue;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not get criterion name for matching", e);
        }

        return null;
    }

    /**
     * Gets the original weight from a criterion node.
     */
    private Integer getOriginalWeight(Node originalCriterion) {
        try {
            String weightStr = XmlUtils.getNodeValueAtPath(originalCriterion, Constants.PATH_IN_AWARD_CRITERION_WEIGHT);
            if (weightStr != null && !weightStr.trim().isEmpty() && !Constants.PLACEHOLDER_WEIGHT.equals(weightStr)) {
                return Integer.parseInt(weightStr.trim());
            }
        } catch (Exception e) {
            logger.debug("Could not parse original weight", e);
        }
        return null;
    }

    /**
     * Distributes weights among new and existing criteria according to the rules.
     */
    private void distributeWeights(List<Node> newCriteria, List<Node> existingCriteria,
            Map<Node, Integer> originalWeights) {

        // Calculate weights for new criteria (from patches)
        if (!newCriteria.isEmpty()) {
            int weightPerNew = Constants.DEFAULT_WEIGHT_GPP_CRITERIA / newCriteria.size();
            int remainder = Constants.DEFAULT_WEIGHT_GPP_CRITERIA % newCriteria.size();

            for (int i = 0; i < newCriteria.size(); i++) {
                int weight = weightPerNew + (i < remainder ? 1 : 0);
                updateCriterionWeight(newCriteria.get(i), weight);
            }

            logger.debug("Assigned weights to {} new criteria (total: {})",
                    newCriteria.size(), Constants.DEFAULT_WEIGHT_GPP_CRITERIA);
        }

        // Calculate weights for existing criteria
        if (!existingCriteria.isEmpty()) {
            int totalWeightForExisting = 100 - Constants.DEFAULT_WEIGHT_GPP_CRITERIA;
            distributeExistingCriteriaWeights(existingCriteria, originalWeights, totalWeightForExisting);
        }
    }

    /**
     * Distributes weights among existing criteria proportionally to their original
     * weights.
     */
    private void distributeExistingCriteriaWeights(List<Node> existingCriteria,
            Map<Node, Integer> originalWeights,
            int totalWeight) {

        // Calculate total original weight
        int totalOriginalWeight = 0;
        boolean hasOriginalWeights = false;

        for (Node criterion : existingCriteria) {
            Integer originalWeight = originalWeights.get(criterion);
            if (originalWeight != null) {
                totalOriginalWeight += originalWeight;
                hasOriginalWeights = true;
            }
        }

        if (hasOriginalWeights && totalOriginalWeight > 0) {
            // Distribute proportionally to original weights
            int distributedWeight = 0;

            for (int i = 0; i < existingCriteria.size(); i++) {
                Node criterion = existingCriteria.get(i);
                Integer originalWeight = originalWeights.get(criterion);

                int newWeight;
                if (originalWeight != null) {
                    if (i == existingCriteria.size() - 1) {
                        // Last criterion gets remaining weight to ensure total is exact
                        newWeight = totalWeight - distributedWeight;
                    } else {
                        newWeight = Math.round((float) originalWeight * totalWeight / totalOriginalWeight);
                    }
                } else {
                    // If no original weight, distribute evenly among remaining criteria
                    int remaining = existingCriteria.size() - i;
                    newWeight = (totalWeight - distributedWeight) / remaining;
                }

                updateCriterionWeight(criterion, newWeight);
                distributedWeight += newWeight;
            }

            logger.debug("Distributed weights to {} existing criteria proportionally (total: {})",
                    existingCriteria.size(), totalWeight);
        } else {
            // No original weights available, distribute evenly
            int weightPerCriterion = totalWeight / existingCriteria.size();
            int remainder = totalWeight % existingCriteria.size();

            for (int i = 0; i < existingCriteria.size(); i++) {
                int weight = weightPerCriterion + (i < remainder ? 1 : 0);
                updateCriterionWeight(existingCriteria.get(i), weight);
            }

            logger.debug("Distributed weights to {} existing criteria evenly (total: {})",
                    existingCriteria.size(), totalWeight);
        }
    }

    /**
     * Updates the weight value for a specific criterion node.
     */
    private void updateCriterionWeight(Node criterionNode, int weight) {
        try {
            Node weightNode = XmlUtils.getNodeAtPath(criterionNode, Constants.PATH_IN_AWARD_CRITERION_WEIGHT);
            if (weightNode != null) {
                weightNode.setTextContent(String.valueOf(weight));
                logger.debug("Updated criterion weight to: {}", weight);
            } else {
                logger.warn("Weight node not found in criterion, could not update weight");
            }
        } catch (Exception e) {
            logger.error("Failed to update criterion weight", e);
        }
    }
}
