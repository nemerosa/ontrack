package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.model.structure.Branch;

public abstract class AbstractBranchJob implements Job {

    private final Branch branch;

    protected AbstractBranchJob(Branch branch) {
        this.branch = branch;
    }

    @Override
    public boolean isDisabled() {
        return branch.isDisabled() || branch.getProject().isDisabled();
    }

}
