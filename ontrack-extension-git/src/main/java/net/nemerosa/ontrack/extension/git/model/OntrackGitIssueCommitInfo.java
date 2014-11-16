package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OntrackGitIssueCommitInfo {

    private final GitUICommit uiCommit;
    private final List<OntrackGitIssueCommitBranchInfo> branchInfos;

    public static OntrackGitIssueCommitInfo of(GitUICommit uiCommit) {
        return new OntrackGitIssueCommitInfo(
                uiCommit,
                new ArrayList<>()
        );
    }

    public void add(OntrackGitIssueCommitBranchInfo branchInfo) {
        branchInfos.add(branchInfo);
    }
}
