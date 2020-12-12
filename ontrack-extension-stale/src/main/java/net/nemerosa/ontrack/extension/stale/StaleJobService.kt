package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.structure.Project

interface StaleJobService : JobOrchestratorSupplier {

    /**
     * If the project is configured for stale branches, applies its policy to all its branches.
     *
     * @param runListener Listener (for logging)
     * @param project     Project to scan
     */
    fun detectAndManageStaleBranches(runListener: JobRunListener, project: Project)

}