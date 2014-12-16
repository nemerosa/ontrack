package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Data that can be collected around an issue.
 */
@Data
public class OntrackSVNIssueRevisionInfo {

    /**
     * Revision information
     */
    private final SVNChangeLogRevision revisionInfo;

    /**
     * List of associated branches
     */
    private final List<SCMIssueCommitBranchInfo> branchInfos;

    /**
     * Builder
     */
    public static OntrackSVNIssueRevisionInfo of(SVNChangeLogRevision revisionInfo) {
        return new OntrackSVNIssueRevisionInfo(
                revisionInfo,
                new ArrayList<>()
        );
    }

    /**
     * Associates a new branch to this revision
     */
    public void add(SCMIssueCommitBranchInfo branchInfo) {
        branchInfos.add(branchInfo);
    }

}
