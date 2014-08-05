package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;

/**
 * Data that can be collected around an issue.
 */
@Data
public class OntrackGitIssueInfo {

    /**
     * Associated repository configuration
     */
    private final GitConfiguration configuration;

    /**
     * Associated issue configuration
     */
    private final IssueServiceConfigurationRepresentation issueServiceConfigurationRepresentation;

    /**
     * Associated issue
     */
    private final Issue issue;

    /**
     * Primary commit information
     */
    private final OntrackGitCommitInfo commitInfo;

    /**
     * Log
     */
    private final GitUILog log;

}
