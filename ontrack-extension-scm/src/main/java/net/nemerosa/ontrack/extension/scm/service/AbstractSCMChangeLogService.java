package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.model.structure.BuildView;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StructureService;

public abstract class AbstractSCMChangeLogService {

    protected final StructureService structureService;

    protected AbstractSCMChangeLogService(StructureService structureService) {
        this.structureService = structureService;
    }

    protected BuildView getBuildView(ID id) {
        return structureService.getBuildView(structureService.getBuild(id));
    }

}
