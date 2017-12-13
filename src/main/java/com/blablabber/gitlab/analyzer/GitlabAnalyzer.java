package com.blablabber.gitlab.analyzer;

import com.blablabber.analysis.pmd.PmdAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class GitlabAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabAnalyzer.class);
    private final PmdAnalyzer pmdAnalyzer;

    @Autowired
    private GitlabAnalyzer(PmdAnalyzer pmdAnalyzer) {
        this.pmdAnalyzer = pmdAnalyzer;
    }

    MergeRequestAnalysisResult analyze(MergeRequestFileCollector mergeRequestFileCollector) {
        LOGGER.info("Analyzing merge request {}", mergeRequestFileCollector.getGitLabMergeRequest());
        List<String> sourceViolations = pmdAnalyzer.analyze(mergeRequestFileCollector.getSourceDirectory().toString());
        List<String> targetViolations = pmdAnalyzer.analyze(mergeRequestFileCollector.getTargetDirectory().toString());
        LOGGER.info("Analyzing merge request {} finished", mergeRequestFileCollector.getGitLabMergeRequest());
        return new MergeRequestAnalysisResult(mergeRequestFileCollector.getGitLabMergeRequest(), sourceViolations, targetViolations);
    }

}
