package net.nemerosa.ontrack.extension.svn.db;

import lombok.Data;

/**
 * Data that can be collected around an issue.
 */
@Data
public class SVNIssueInfo {

    /**
     * Empty issue info.
     */
    public static SVNIssueInfo empty() {
        return new SVNIssueInfo();
    }

}
