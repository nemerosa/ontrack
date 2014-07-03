package net.nemerosa.ontrack.extension.svn.db;

import net.nemerosa.ontrack.extension.svn.model.SVNLocation;

import java.util.List;
import java.util.function.Predicate;

public interface SVNEventDao {

    void createCopyEvent(int repositoryId, long revision, String copyFromPath, long copyFromRevision, String copyToPath);

    void createStopEvent(int repositoryId, long revision, String path);

    TCopyEvent getLastCopyEvent(int repositoryId, String path, long revision);

    SVNLocation getFirstCopyAfter(int repositoryId, SVNLocation location);

    List<TCopyEvent> findCopies(int repositoryId, String fromPath, String toPathPrefix, Predicate<TCopyEvent> filter);
}
