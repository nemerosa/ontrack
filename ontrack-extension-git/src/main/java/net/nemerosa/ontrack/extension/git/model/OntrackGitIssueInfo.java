package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;

import java.util.List;

/**
 * Data that can be collected around an issue.
 */
@Data
public class OntrackGitIssueInfo {

    /**
     * Associated issue configuration
     */
    private final IssueServiceConfigurationRepresentation issueServiceConfigurationRepresentation;

    /**
     * Associated issue
     */
    private final Issue issue;

    /**
     * Last commit per branch
     */
    private final List<OntrackGitIssueCommitInfo> commitInfos;

}
