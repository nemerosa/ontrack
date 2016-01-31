package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo;

import java.util.ArrayList;
import java.util.List;

@Data
public class OntrackGitIssueCommitInfo {

    private final GitUICommit uiCommit;
    private final List<SCMIssueCommitBranchInfo> branchInfos;

    public static OntrackGitIssueCommitInfo of(GitUICommit uiCommit) {
        return new OntrackGitIssueCommitInfo(
                uiCommit,
                new ArrayList<>()
        );
    }

    public void add(SCMIssueCommitBranchInfo branchInfo) {
        branchInfos.add(branchInfo);
    }
}
