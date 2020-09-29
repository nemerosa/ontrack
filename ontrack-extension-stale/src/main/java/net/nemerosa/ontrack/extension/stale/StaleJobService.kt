package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import java.time.LocalDateTime

interface StaleJobService : JobOrchestratorSupplier {

    /**
     * If the project is configured for stale branches, applies its policy to all its branches.
     *
     * @param runListener Listener (for logging)
     * @param project     Project to scan
     */
    fun detectAndManageStaleBranches(runListener: JobRunListener, project: Project)

    /**
     * Applies the given retention times to the branch.
     *
     * @param branch           Branch to manage
     * @param disablingTime    Time before which the branch must be disabled (null if not applicable)
     * @param deletionTime     Time before which the branch must be deleted (null if not applicable)
     * @param promotionsToKeep List of promotions to keep (if the branch has one of those promotions, it cannot be
     * disabled or removed). Note that the list might be null or empty.
     */
    fun detectAndManageStaleBranch(branch: Branch, disablingTime: LocalDateTime?, deletionTime: LocalDateTime?, promotionsToKeep: List<String>?)

}