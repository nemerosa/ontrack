package net.nemerosa.ontrack.extension.scm.changelog;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.model.structure.StructureService;

public abstract class AbstractSCMChangeLogService {

    private final StructureService structureService;

    protected AbstractSCMChangeLogService(StructureService structureService) {
        this.structureService = structureService;
    }

    protected SCMChangeLog defaultChangeLog(BuildDiffRequest request) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.changelog.SVNChangeLogServiceImpl.defaultChangeLog
        return null;
    }

}
