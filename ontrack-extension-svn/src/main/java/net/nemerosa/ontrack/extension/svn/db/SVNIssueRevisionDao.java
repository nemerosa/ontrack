package net.nemerosa.ontrack.extension.svn.db;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public interface SVNIssueRevisionDao {

    void link(int repositoryId, long revision, String issueKey);

    List<String> findIssuesByRevision(int repositoryId, long revision);

    Optional<String> findIssueByKey(int repositoryId, String issueKey);

    List<Long> findRevisionsByIssue(int repositoryId, String issueKey);

    OptionalLong findLastRevisionByIssue(int repositoryId, String issueKey);

}
