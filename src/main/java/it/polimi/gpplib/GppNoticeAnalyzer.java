package it.polimi.gpplib;

import it.polimi.gpplib.model.Notice;
import it.polimi.gpplib.model.SuggestedGppCriterion;
import it.polimi.gpplib.model.GppAnalysisResult;
import it.polimi.gpplib.model.SuggestedGppPatch;
import java.util.List;

// TODO: consider separating these methods into different interfaces
public interface GppNoticeAnalyzer {
    Notice loadNotice(String xmlString);

    GppAnalysisResult analyzeNotice(Notice notice);

    List<SuggestedGppPatch> suggestPatches(Notice notice, List<SuggestedGppCriterion> suggestedCriteria);

    Notice applyPatches(Notice notice, List<SuggestedGppPatch> patches);
}
