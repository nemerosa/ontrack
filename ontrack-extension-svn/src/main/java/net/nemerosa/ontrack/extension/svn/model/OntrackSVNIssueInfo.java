package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;

import java.util.Collections;
import java.util.List;

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
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
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
     * Last revision per branch
     */
    private final List<OntrackSVNIssueRevisionInfo> revisionInfos;

    /**
     * Merged revision information
     */
    @Deprecated
    private final List<OntrackSVNRevisionInfo> mergedRevisionInfos;

    /**
     * Other revisions
     */
    @Deprecated
    private final List<SVNChangeLogRevision> revisions;

}
