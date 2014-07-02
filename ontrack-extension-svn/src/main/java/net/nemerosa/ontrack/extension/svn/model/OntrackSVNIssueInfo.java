package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;

/**
 * Data that can be collected around an issue.
 */
@Data
public class OntrackSVNIssueInfo {

    /**
     * Empty issue info.
     */
    public static OntrackSVNIssueInfo empty(SVNConfiguration configuration) {
        return new OntrackSVNIssueInfo(
                configuration,
                null,
                null,
                null
        );
    }

    /**
     * Associated repository configuration
     */
    private final SVNConfiguration configuration;

    /**
     * Associated issue configuration
     */
    private final IssueServiceConfigurationRepresentation issueServiceConfigurationRepresentation;

    /**
     * Associated issue
     */
    private final Issue issue;

    /**
     * Primary revision information
     */
    private final OntrackSVNRevisionInfo revisionInfo;

}
