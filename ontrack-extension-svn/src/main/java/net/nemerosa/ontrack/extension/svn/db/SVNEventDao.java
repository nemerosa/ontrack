package net.nemerosa.ontrack.extension.svn.db;

public interface SVNEventDao {

    void createCopyEvent(int repositoryId, long revision, String copyFromPath, long copyFromRevision, String copyToPath);

    void createStopEvent(int repositoryId, long revision, String path);
}
