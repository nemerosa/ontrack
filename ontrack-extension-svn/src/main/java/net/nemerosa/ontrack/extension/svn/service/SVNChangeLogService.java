package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.issues.export.ExportFormat;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;

public interface SVNChangeLogService {

    SVNChangeLog changeLog(BuildDiffRequest request);

    SVNChangeLogRevisions getChangeLogRevisions(SVNChangeLog changeLog);

    SVNChangeLogIssues getChangeLogIssues(SVNChangeLog changeLog);

    SVNChangeLogFiles getChangeLogFiles(SVNChangeLog changeLog);

    SVNHistory getBuildSVNHistory(SVNRepository svnRepository, Build build);

    Collection<ExportFormat> changeLogExportFormats(ID branchId);
}
