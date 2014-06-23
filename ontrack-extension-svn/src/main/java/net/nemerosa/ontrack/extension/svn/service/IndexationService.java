package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.LastRevisionInfo;
import net.nemerosa.ontrack.extension.svn.model.IndexationRange;

public interface IndexationService {

    boolean isIndexationRunning(String name);

    void indexFromLatest(String name);

    void indexRange(String name, IndexationRange range);

    void reindex(String name);

    LastRevisionInfo getLastRevisionInfo(String name);
}
