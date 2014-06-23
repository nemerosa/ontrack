package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLog;

public interface SVNChangeLogService {

    SVNChangeLog changeLog(BuildDiffRequest request);

}
