package net.nemerosa.ontrack.extension.stale;

import net.nemerosa.ontrack.job.JobRunListener;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;

import java.time.LocalDateTime;

public interface StaleJobService extends JobOrchestratorSupplier {

    /**
     * If the project is configured for stale branches, applies its policy to all its branches.
     *
     * @param runListener Listener (for logging)
     * @param project     Project to scan
     */
    void detectAndManageStaleBranches(JobRunListener runListener, Project project);

    /**
     * Applies the given retention times to the branch.
     *
     * @param branch        Branch to manage
     * @param disablingTime Time before which the branch must be disabled (null if not applicable)
     * @param deletionTime  Time before which the branch must be deleted (null if not applicable)
     */
    void detectAndManageStaleBranch(Branch branch, LocalDateTime disablingTime, LocalDateTime deletionTime);

}
