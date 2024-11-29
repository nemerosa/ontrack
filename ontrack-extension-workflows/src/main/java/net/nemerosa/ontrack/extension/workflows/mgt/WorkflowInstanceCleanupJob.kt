package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.repository.WorkflowInstanceRepository
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
class WorkflowInstanceCleanupJob(
    private val cachedSettingsService: CachedSettingsService,
    private val workflowInstanceRepository: WorkflowInstanceRepository,
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
            val settings = cachedSettingsService.getCachedSettings(WorkflowSettings::class.java)
            val time = Time.now - Duration.of(settings.retentionDuration, ChronoUnit.MILLIS)
            workflowInstanceRepository.cleanup(time)
        }

        override fun getDescription(): String = "Cleanup of workflow instances past their retention time"

        override fun isDisabled(): Boolean = false

    }
}