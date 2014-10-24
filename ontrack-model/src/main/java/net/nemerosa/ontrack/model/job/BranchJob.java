package net.nemerosa.ontrack.model.job;

import net.nemerosa.ontrack.model.structure.Branch;

public abstract class BranchJob implements Job {

    private final Branch branch;

    protected BranchJob(Branch branch) {
        this.branch = branch;
    }

    @Override
    public boolean isDisabled() {
        return branch.isDisabled() || branch.getProject().isDisabled();
    }

}
