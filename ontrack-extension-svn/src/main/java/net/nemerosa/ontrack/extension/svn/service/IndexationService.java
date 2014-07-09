package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.IndexationRange;
import net.nemerosa.ontrack.extension.svn.model.LastRevisionInfo;
import net.nemerosa.ontrack.model.Ack;

public interface IndexationService {

    Ack indexFromLatest(String name);

    Ack indexRange(String name, IndexationRange range);

    Ack reindex(String name);

    LastRevisionInfo getLastRevisionInfo(String name);
}
