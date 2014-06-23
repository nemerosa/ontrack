package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLog;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLogIssues;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLogRevisions;

public interface SVNChangeLogService {

    SVNChangeLog changeLog(BuildDiffRequest request);

    SVNChangeLogRevisions getChangeLogRevisions(SVNChangeLog changeLog);

    SVNChangeLogIssues getChangeLogIssues(SVNChangeLog changeLog);
}
