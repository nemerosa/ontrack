package net.nemerosa.ontrack.extension.svn.indexation;

import net.nemerosa.ontrack.extension.svn.LastRevisionInfo;

public interface IndexationService {

    boolean isIndexationRunning(String name);

    void reindex(String name);

    LastRevisionInfo getLastRevisionInfo(String name);
}
