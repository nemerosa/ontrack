package net.nemerosa.ontrack.extension.svn.db;

import java.time.LocalDateTime;

public interface SVNRevisionDao {

    long getLast(int repositoryId);

    void addRevision(int repositoryId, long revision, String author, LocalDateTime dateTime, String message, String branch);

}
