package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStore
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class WorkflowInstanceCleanupJob(
    private val workflowInstanceStore: WorkflowInstanceStore,
) : JobProvider {
    override fun getStartingJobs(): Collection<JobRegistration> =
        listOf(
            createWorkflowInstanceCleanupJobRegistration()
        )

    private fun createWorkflowInstanceCleanupJobRegistration() = JobRegistration(
        job = createWorkflowInstanceCleanupJob(),
        schedule = Schedule.EVERY_DAY,
    )

    private fun createWorkflowInstanceCleanupJob() = object : Job {

        override fun getKey(): JobKey =
            WorkflowJobs.TYPE_CLEANUP.getKey("main")

        override fun getTask() = JobRun {
            workflowInstanceStore.cleanup()
        }

        override fun getDescription(): String = "Cleanup of workflow instances past their retention time"

        override fun isDisabled(): Boolean = false

    }
}