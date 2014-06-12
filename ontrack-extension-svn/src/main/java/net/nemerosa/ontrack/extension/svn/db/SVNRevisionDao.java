package net.nemerosa.ontrack.extension.svn.db;

public interface SVNRevisionDao {

    long getLast(int repositoryId);
    
}
