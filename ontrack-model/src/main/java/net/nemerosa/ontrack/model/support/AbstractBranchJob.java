package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.StructureService;

public abstract class AbstractBranchJob implements Job {

    private final StructureService structureService;
    private final Branch branch;

    protected AbstractBranchJob(StructureService structureService, Branch branch) {
        this.structureService = structureService;
        this.branch = branch;
    }

    @Override
    public boolean isDisabled() {
        return branch.isDisabled() || branch.getProject().isDisabled();
    }

    @Override
    public boolean isValid() {
        return structureService.findBranchByName(branch.getProject().getName(), branch.getName()).isPresent();
    }

}
