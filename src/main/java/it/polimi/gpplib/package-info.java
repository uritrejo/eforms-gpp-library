/**
 * Core package for the Green Public Procurement (GPP) library.
 * 
 * <p>
 * This library provides tools for analyzing and improving public procurement
 * notices
 * to enhance their environmental sustainability characteristics through Green
 * Public Procurement
 * criteria and recommendations.
 * 
 * <p>
 * The main entry point is the {@link it.polimi.gpplib.GppNoticeAnalyzer}
 * interface,
 * with its default implementation
 * {@link it.polimi.gpplib.DefaultGppNoticeAnalyzer}.
 * 
 * <p>
 * Key features include:
 * <ul>
 * <li>Loading and parsing procurement notices from XML</li>
 * <li>Analyzing notices to identify relevant GPP criteria</li>
 * <li>Suggesting improvements and patches for better GPP compliance</li>
 * <li>Applying patches to create enhanced notice versions</li>
 * </ul>
 * 
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * // Create analyzer with default configuration
 * GppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer();
 * 
 * // Load a notice from XML
 * Notice notice = analyzer.loadNotice(xmlString);
 * 
 * // Analyze the notice
 * GppAnalysisResult result = analyzer.analyzeNotice(notice);
 * 
 * // Get suggested patches
 * List<SuggestedGppPatch> patches = analyzer.suggestPatches(notice, result.getSuggestedCriteria());
 * 
 * // Apply patches to improve the notice
 * Notice improvedNotice = analyzer.applyPatches(notice, patches);
 * }</pre>
 * 
 * @author Politecnico di Milano
 * @version 1.0
 * @since 1.0
 */
package it.polimi.gpplib;
