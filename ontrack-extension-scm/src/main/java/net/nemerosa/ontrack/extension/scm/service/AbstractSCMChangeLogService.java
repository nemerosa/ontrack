package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.model.structure.*;

import java.util.List;

public abstract class AbstractSCMChangeLogService {

    protected final StructureService structureService;
    protected final PropertyService propertyService;

    protected AbstractSCMChangeLogService(StructureService structureService, PropertyService propertyService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
    }

    protected BuildView getBuildView(ID id) {
        return structureService.getBuildView(structureService.getBuild(id));
    }


    protected void validateIssues(List<? extends SCMChangeLogIssue> issuesList, Branch branch) {
        // FIXME Validation of issues
    }

}
