package net.nemerosa.ontrack.extension.scm.changelog;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.model.structure.StructureService;

public abstract class AbstractSCMChangeLogService {

    private final StructureService structureService;

    protected AbstractSCMChangeLogService(StructureService structureService) {
        this.structureService = structureService;
    }

    protected SCMChangeLog defaultChangeLog(BuildDiffRequest request) {
        return SCMChangeLog.of(
                structureService.getBranch(request.getBranch()),
                structureService.getBuildView(structureService.getBuild(request.getFrom())),
                structureService.getBuildView(structureService.getBuild(request.getTo()))
        );
    }

}
