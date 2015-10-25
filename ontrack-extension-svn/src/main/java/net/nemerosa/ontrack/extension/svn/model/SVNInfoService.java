package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;

public interface SVNInfoService {

    /**
     * Gets information about an issue in a repository.
     *
     * @param configurationName Name of the repository configuration.
     * @param issueKey          Key of the issue
     * @return Information (never null, but can be empty)
     */
    OntrackSVNIssueInfo getIssueInfo(String configurationName, String issueKey);

    /**
     * Gets information about a revision in a repository
     *
     * @param repository Repository to get the info from
     * @param revision   Revision to get information about
     * @return Information (never null, but can be empty)
     */
    OntrackSVNRevisionInfo getOntrackRevisionInfo(SVNRepository repository, long revision);
}
