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
import it.polimi.gpplib.utils.EFormsSdkWrapper;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(DefaultGppNoticeAnalyzer.class);

    private final GppDomainKnowledgeService domainKnowledge;

    private final GppPatchApplier patchApplier;

    private final EFormsSdkWrapper eFormsSdkWrapper;

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
        logger.info("Initializing DefaultGppNoticeAnalyzer with default values");
        try {
            domainKnowledge = new GppDomainKnowledgeService(Constants.DOMAIN_KNOWLEDGE_GPP_DOCS_PATH,
                    Constants.DOMAIN_KNOWLEDGE_GPP_CRITERIA_PATH, Constants.DOMAIN_KNOWLEDGE_GPP_PATCHES_PATH);
            logger.info("Successfully loaded domain knowledge from default paths");
        } catch (Exception e) {
            logger.error("Failed to load domain knowledge from default paths", e);
            throw new GppInternalErrorException("Unexpected error loading domain knowledge", e);
        }

        try {
            eFormsSdkWrapper = new EFormsSdkWrapper(Constants.EFORMS_SDK_DEFAULT_VERSION);
            logger.info("Successfully loaded the default version of the eForms SDK");
        } catch (Exception e) {
            logger.error("Failed to load the eForms SDK", e);
            throw new GppInternalErrorException("Unexpected error loading eForms SDK", e);
        }

        patchApplier = new GppPatchApplier();
        logger.debug("DefaultGppNoticeAnalyzer initialization completed");
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
        logger.info("Initializing DefaultGppNoticeAnalyzer with custom paths: docs={}, criteria={}, patches={}",
                gppDocsPath, gppCriteriaPath, gppPatchesPath);
        try {
            domainKnowledge = new GppDomainKnowledgeService(gppDocsPath, gppCriteriaPath, gppPatchesPath);
            logger.info("Successfully loaded domain knowledge from custom paths");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid path provided for domain knowledge loading", e);
            throw new GppBadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error loading domain knowledge from custom paths", e);
            throw new GppInternalErrorException("Unexpected error loading domain knowledge", e);
        }
        patchApplier = new GppPatchApplier();
        eFormsSdkWrapper = new EFormsSdkWrapper();
        logger.debug("DefaultGppNoticeAnalyzer initialization completed with custom paths");
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
        logger.debug("Loading notice from XML string (length: {} characters)",
                xmlString != null ? xmlString.length() : 0);
        Notice notice;
        try {
            notice = new Notice(xmlString);
            logger.info("Successfully loaded notice with {} lots", notice.getLotIds().size());
        } catch (XmlUtilsException e) {
            logger.error("Failed to parse XML string: {}", e.getMessage());
            throw new GppBadRequestException("Invalid notice xml string: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error loading notice: {}", e.getMessage(), e);
            throw new GppBadRequestException("Unexpected error loading notice: " + e.getMessage(), e);
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
        if (notice == null) {
            throw new GppBadRequestException("Notice must not be null");
        }

        logger.info("Starting analysis of notice with {} lots", notice.getLotIds().size());
        try {
            // TODO: verify that the documents can't be duplicated
            java.util.Set<GppDocument> allRelevantDocuments = new java.util.HashSet<>(); // to avoid duplicates
            List<SuggestedGppCriterion> allSuggestedCriteria = new java.util.ArrayList<>();

            List<String> projectCpvs = notice.getAllProcurementProjectCpvs();
            logger.debug("Found {} project-level CPV codes: {}", projectCpvs.size(), projectCpvs);

            List<String> lotIds = notice.getLotIds();
            logger.debug("Processing {} lots for analysis", lotIds.size());

            for (String lotId : lotIds) {
                logger.debug("Analyzing lot: {}", lotId);
                List<String> lotCpvs = notice.getAllLotCpvs(lotId);
                if (lotCpvs.isEmpty()) {
                    logger.debug("Lot {} has no specific CPVs, using project CPVs", lotId);
                    lotCpvs = projectCpvs;
                } else {
                    logger.debug("Lot {} has {} specific CPV codes: {}", lotId, lotCpvs.size(), lotCpvs);
                }

                List<GppDocument> relevantDocuments = domainKnowledge.getRelevantGppDocuments(lotCpvs);
                List<GppCriterion> relevantCriteria = domainKnowledge.getRelevantGppCriteria(lotCpvs, ambitionLevel);
                List<SuggestedGppCriterion> suggestedCriteria = domainKnowledge
                        .convertToSuggestedGppCriteria(relevantCriteria, lotId, lotCpvs);

                logger.debug("Found {} relevant documents and {} relevant criteria for lot {}",
                        relevantDocuments.size(), relevantCriteria.size(), lotId);

                allRelevantDocuments.addAll(relevantDocuments);
                allSuggestedCriteria.addAll(suggestedCriteria);
            }

            logger.info("Analysis completed. Found {} unique documents and {} suggested criteria",
                    allRelevantDocuments.size(), allSuggestedCriteria.size());
            return new GppAnalysisResult(new java.util.ArrayList<>(allRelevantDocuments), allSuggestedCriteria);
        } catch (Exception e) {
            logger.error("Unexpected error during notice analysis", e);
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
        if (suggestedCriteria == null || notice == null) {
            throw new GppBadRequestException("Notice and suggested criteria must not be null");
        }
        logger.info("Suggesting patches for {} criteria", suggestedCriteria.size());

        // TODO: validate suggestedCriteria & return a GppBadRequestException if invalid
        try {
            List<SuggestedGppPatch> patches = domainKnowledge.suggestGppPatches(notice, suggestedCriteria);
            logger.info("Generated {} patch suggestions", patches.size());
            return patches;
        } catch (Exception e) {
            logger.error("Unexpected error during patch suggestion", e);
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
        logger.info("Applying {} patches to notice", patches.size());
        try {
            int appliedPatches = 0;
            for (SuggestedGppPatch patch : patches) {
                if (patch == null) {
                    logger.debug("Skipping null patch");
                    continue;
                }
                logger.debug("Applying patch: {} for lot {}", patch.getName(), patch.getLotId());
                notice = patchApplier.applyPatch(notice, patch);
                appliedPatches++;
            }
            logger.info("Successfully applied {} out of {} patches", appliedPatches, patches.size());
            return notice;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid patch encountered: {}", e.getMessage(), e);
            throw new GppBadRequestException("Invalid patch: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error applying patches", e);
            throw new GppInternalErrorException("Unexpected error applying patches: " + e.getMessage(), e);
        }
    }
}