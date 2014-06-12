package net.nemerosa.ontrack.extension.svn.indexation;

public interface IndexationService {

    boolean isIndexationRunning(String name);

    void reindex(String name);

}
