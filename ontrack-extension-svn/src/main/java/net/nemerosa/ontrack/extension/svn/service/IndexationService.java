package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.IndexationRange;
import net.nemerosa.ontrack.extension.svn.model.LastRevisionInfo;

public interface IndexationService {

    void indexFromLatest(String name);

    void indexRange(String name, IndexationRange range);

    void reindex(String name);

    LastRevisionInfo getLastRevisionInfo(String name);
}
