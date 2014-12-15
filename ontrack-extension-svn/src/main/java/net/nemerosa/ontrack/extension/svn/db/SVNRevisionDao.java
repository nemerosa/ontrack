package net.nemerosa.ontrack.extension.svn.db;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SVNRevisionDao {

    long getLast(int repositoryId);

    void addRevision(int repositoryId, long revision, String author, LocalDateTime dateTime, String message, String branch);

    void addMergedRevisions(int repositoryId, long revision, List<Long> mergedRevisions);

    TRevision getLastRevision(int repositoryId);

    TRevision get(int repositoryId, long revision);

    List<Long> getMergesForRevision(int repositoryId, long revision);

    Optional<TRevision> getLastRevisionOnBranch(int repositoryId, String branch);
}
