package com.blablabber.gitlab.api;

import java.util.List;

public class GitLabMergeRequestChanges {

    private List<Change> changes;

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    @Override
    public String toString() {
        String sb = "GitLabMergeRequestChanges{" + "changes=" + changes +
                '}';
        return sb;
    }
}