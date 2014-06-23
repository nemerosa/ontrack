package net.nemerosa.ontrack.extension.svn.db;

import java.util.List;

public interface SVNIssueRevisionDao {

    void link(int repositoryId, long revision, String issueKey);

    List<String> findIssuesByRevision(int repositoryId, long revision);
}
