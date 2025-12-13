package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.StructureService;

public abstract class AbstractProjectEntityController {

    protected final StructureService structureService;

    public AbstractProjectEntityController(StructureService structureService) {
        this.structureService = structureService;
    }

    protected ProjectEntity getEntity(ProjectEntityType entityType, ID id) {
        return entityType.getEntityFn(structureService).apply(id);
    }
}
