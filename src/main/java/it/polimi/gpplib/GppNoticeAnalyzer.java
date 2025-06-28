package it.polimi.gpplib;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.SuggestedGppPatch;
import java.util.List;

/**
 * Main interface for analyzing GPP (Green Public Procurement) notices.
 * This interface provides methods to load, analyze, and apply patches to public
 * procurement notices
 * to improve their environmental sustainability characteristics.
 * 
 * <p>
 * The typical workflow involves:
 * <ol>
 * <li>Loading a notice from XML using {@link #loadNotice(String)}</li>
 * <li>Analyzing the notice to identify GPP criteria using
 * {@link #analyzeNotice(Notice)}</li>
 * <li>Suggesting patches to improve GPP compliance using
 * {@link #suggestPatches(Notice, List)}</li>
 * <li>Applying the patches to create an improved notice using
 * {@link #applyPatches(Notice, List)}</li>
 * </ol>
 * 
 * @author Politecnico di Milano
 * @version 1.0
 * @since 1.0
 */
// TODO: consider separating these methods into different interfaces
public interface GppNoticeAnalyzer {

    /**
     * Loads a procurement notice from an XML string.
     * 
     * @param xmlString the XML representation of the procurement notice
     * @return a {@link Notice} object representing the parsed procurement notice
     * @throws GppException if the XML cannot be parsed or is invalid
     */
    Notice loadNotice(String xmlString);

    /**
     * Analyzes a procurement notice to identify existing GPP criteria and suggest
     * improvements.
     * 
     * @param notice the procurement notice to analyze
     * @return a {@link GppAnalysisResult} containing the analysis results and
     *         suggested criteria
     * @throws GppException if the analysis fails
     */
    GppAnalysisResult analyzeNotice(Notice notice);

    /**
     * Suggests specific patches to improve the GPP compliance of a procurement
     * notice.
     * 
     * @param notice            the original procurement notice
     * @param suggestedCriteria the list of suggested GPP criteria to implement
     * @return a list of {@link SuggestedGppPatch} objects representing the
     *         recommended changes
     * @throws GppException if patch suggestion fails
     */
    List<SuggestedGppPatch> suggestPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria);

    /**
     * Applies a list of patches to a procurement notice to create an improved
     * version.
     * 
     * @param notice  the original procurement notice
     * @param patches the list of patches to apply
     * @return a new {@link Notice} object with the patches applied
     * @throws GppException if patch application fails
     */
    Notice applyPatches(Notice notice, List<SuggestedGppPatch> patches);
}
