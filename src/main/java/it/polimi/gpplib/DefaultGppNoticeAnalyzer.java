package it.polimi.gpplib;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.Constants;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.GppCriterion;
import it.polimi.gpplib.model.GppDocument;
import it.polimi.gpplib.model.SuggestedGppPatch;
import it.polimi.gpplib.utils.GppDomainKnowledgeService;
import it.polimi.gpplib.utils.GppPatchApplier;
import it.polimi.gpplib.utils.XmlUtils.XmlUtilsException;

import java.util.List;

/**
 * Default implementation of the {@link GppNoticeAnalyzer} interface.
 * 
 * <p>
 * This class provides a complete implementation for analyzing public
 * procurement notices
 * and suggesting Green Public Procurement (GPP) improvements. It uses domain
 * knowledge
 * from GPP documents, criteria, and patches to provide comprehensive analysis
 * and recommendations.
 * 
 * <p>
 * The analyzer operates by:
 * <ul>
 * <li>Loading domain knowledge from configurable file paths</li>
 * <li>Analyzing notices based on their CPV codes and lot structure</li>
 * <li>Suggesting relevant GPP criteria and patches based on procurement
 * categories</li>
 * <li>Applying patches to create improved versions of notices</li>
 * </ul>
 * 
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * GppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer();
 * Notice notice = analyzer.loadNotice(xmlString);
 * GppAnalysisResult result = analyzer.analyzeNotice(notice);
 * List<SuggestedGppPatch> patches = analyzer.suggestPatches(notice, result.getSuggestedCriteria());
 * Notice improvedNotice = analyzer.applyPatches(notice, patches);
 * }</pre>
 * 
 * @author Politecnico di Milano
 * @version 1.0
 * @since 1.0
 */
public class DefaultGppNoticeAnalyzer implements GppNoticeAnalyzer {

    private final GppDomainKnowledgeService domainKnowledge;

    private final GppPatchApplier patchApplier;

    // TODO: eventually, this should come from a config
    /**
     * The ambition level for GPP criteria selection. Currently set to CORE level.
     */
    private String ambitionLevel = GppCriterion.AMBITION_LEVEL_CORE;

    /**
     * Creates a new DefaultGppNoticeAnalyzer with default domain knowledge paths.
     * Uses the default paths defined in {@link Constants} for loading GPP
     * documents,
     * criteria, and patches.
     * 
     * @throws GppInternalErrorException if the domain knowledge cannot be loaded
     */
    public DefaultGppNoticeAnalyzer() {
        try {
            domainKnowledge = new GppDomainKnowledgeService(Constants.DOMAIN_KNOWLEDGE_GPP_DOCS_PATH,
                    Constants.DOMAIN_KNOWLEDGE_GPP_CRITERIA_PATH, Constants.DOMAIN_KNOWLEDGE_GPP_PATCHES_PATH);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error loading domain knowledge", e);
        }
        patchApplier = new GppPatchApplier();
    }

    /**
     * Creates a new DefaultGppNoticeAnalyzer with custom domain knowledge paths.
     * This constructor allows for custom configuration of the domain knowledge
     * sources.
     * 
     * @param gppDocsPath     the file path to GPP documents
     * @param gppCriteriaPath the file path to GPP criteria definitions
     * @param gppPatchesPath  the file path to GPP patches
     * @throws GppBadRequestException    if any of the provided paths are invalid
     * @throws GppInternalErrorException if the domain knowledge cannot be loaded
     */
    // TODO: eventually you'll need to take in config params
    public DefaultGppNoticeAnalyzer(String gppDocsPath, String gppCriteriaPath, String gppPatchesPath) {
        try {
            domainKnowledge = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
        } catch (IllegalArgumentException e) {
            throw new GppBadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error loading domain knowledge", e);
        }
        patchApplier = new GppPatchApplier();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This implementation parses the XML string into a {@link Notice} object and
     * performs
     * basic validation. The XML should conform to the expected procurement notice
     * format.
     * 
     * @param xmlString {@inheritDoc}
     * @return {@inheritDoc}
     * @throws GppBadRequestException if the XML string is malformed or invalid
     */
    @Override
    public Notice loadNotice(String xmlString) {
        Notice notice;
        try {
            notice = new Notice(xmlString);
        } catch (XmlUtilsException e) {
            throw new GppBadRequestException("Invalid notice xml string: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new GppBadRequestException("Unexpecter error loading notice: " + e.getMessage(), e);
        }

        // TODO: add some notice validation (e.g. noticeType, version, language, etc.)

        return notice;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This implementation analyzes the notice by examining all procurement lots and
     * their
     * associated CPV codes. For each lot, it:
     * <ul>
     * <li>Identifies relevant GPP documents based on CPV codes</li>
     * <li>Determines applicable GPP criteria for the current ambition level</li>
     * <li>Converts criteria to suggested criteria with lot-specific context</li>
     * </ul>
     * 
     * <p>
     * If a lot has no specific CPV codes, it falls back to using the project-level
     * CPVs.
     * 
     * @param notice {@inheritDoc}
     * @return {@inheritDoc}
     * @throws GppInternalErrorException if an unexpected error occurs during
     *                                   analysis
     */
    @Override
    public GppAnalysisResult analyzeNotice(Notice notice) {
        try {
            // TODO: verify that the documents can't be duplicated
            java.util.Set<GppDocument> allRelevantDocuments = new java.util.HashSet<>(); // to avoid duplicates
            List<SuggestedGppCriterion> allSuggestedCriteria = new java.util.ArrayList<>();

            List<String> projectCpvs = notice.getAllProcurementProjectCpvs();
            List<String> lotIds = notice.getLotIds();
            for (String lotId : lotIds) {
                List<String> lotCpvs = notice.getAllLotCpvs(lotId);
                if (lotCpvs.isEmpty()) {
                    lotCpvs = projectCpvs;
                }

                List<GppDocument> relevantDocuments = domainKnowledge.getRelevantGppDocuments(lotCpvs);
                List<GppCriterion> relevantCriteria = domainKnowledge.getRelevantGppCriteria(lotCpvs, ambitionLevel);
                List<SuggestedGppCriterion> suggestedCriteria = domainKnowledge
                        .convertToSuggestedGppCriteria(relevantCriteria, lotId, lotCpvs);

                allRelevantDocuments.addAll(relevantDocuments);
                allSuggestedCriteria.addAll(suggestedCriteria);
            }
            return new GppAnalysisResult(new java.util.ArrayList<>(allRelevantDocuments), allSuggestedCriteria);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error during notice analysis: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This implementation uses the domain knowledge service to generate specific
     * patches
     * based on the suggested criteria and the current state of the notice.
     * 
     * @param notice            {@inheritDoc}
     * @param suggestedCriteria {@inheritDoc}
     * @return {@inheritDoc}
     * @throws GppInternalErrorException if an unexpected error occurs during patch
     *                                   suggestion
     */
    @Override
    public List<SuggestedGppPatch> suggestPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria) {
        // TODO: validate suggestedCriteria & return a GppBadRequestException if invalid
        try {
            return domainKnowledge.suggestGppPatches(notice, suggestedCriteria);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error during patch suggestion: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This implementation applies patches sequentially to the notice. Each patch
     * modifies the notice XML structure to add or enhance GPP-related requirements.
     * Null patches in the list are safely ignored.
     * 
     * @param notice  {@inheritDoc}
     * @param patches {@inheritDoc}
     * @return {@inheritDoc}
     * @throws GppBadRequestException    if any patch contains invalid data
     * @throws GppInternalErrorException if an unexpected error occurs during patch
     *                                   application
     */
    @Override
    public Notice applyPatches(Notice notice, List<SuggestedGppPatch> patches) {
        try {
            for (SuggestedGppPatch patch : patches) {
                if (patch == null) {
                    continue;
                }
                notice = patchApplier.applyPatch(notice, patch);
            }
            return notice;
        } catch (IllegalArgumentException e) {
            throw new GppBadRequestException("Invalid patch: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new GppInternalErrorException("Unexpected error applying patches: " + e.getMessage(), e);
        }
    }
}