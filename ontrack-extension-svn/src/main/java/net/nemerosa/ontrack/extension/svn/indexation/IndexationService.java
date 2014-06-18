package net.nemerosa.ontrack.extension.svn.indexation;

import net.nemerosa.ontrack.extension.svn.LastRevisionInfo;

public interface IndexationService {

    boolean isIndexationRunning(String name);

    void indexFromLatest(String name);

    void indexRange(String name, long from, long to);

    void reindex(String name);

    LastRevisionInfo getLastRevisionInfo(String name);
}
