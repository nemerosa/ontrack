package net.nemerosa.ontrack.extension.svn.db;

public interface SVNIssueRevisionDao {

    void link(int repositoryId, long revision, String issueKey);

}
