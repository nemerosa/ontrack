package net.nemerosa.ontrack.extension.svn.changelog;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;

public interface SVNChangeLogService {

    SVNChangeLog changeLog(BuildDiffRequest request);

}
