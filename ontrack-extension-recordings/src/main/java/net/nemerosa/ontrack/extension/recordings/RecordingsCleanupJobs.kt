package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import org.springframework.stereotype.Component

@Component
class RecordingsCleanupJobs(
        private val extensionManager: ExtensionManager,
        private val recordingsCleanupService: RecordingsCleanupService,
) : JobOrchestratorSupplier {

    override val jobRegistrations: Collection<JobRegistration>
        get() = extensionManager.getExtensions(RecordingsExtension::class.java)
                .map { extension ->
                    createCleanupJobRegistration(extension)
                }

    private fun createCleanupJobRegistration(extension: RecordingsExtension<*, *>) = JobRegistration(
            job = createCleanupJob(extension),
            schedule = Schedule.EVERY_DAY,
    )

    private fun <R : Recording, F : Any> createCleanupJob(extension: RecordingsExtension<R, F>) = RecordingsCleanupJob(extension)

    inner class RecordingsCleanupJob<R : Recording, F : Any>(
            private val extension: RecordingsExtension<R, F>
    ) : Job {

        override fun getKey(): JobKey =
                RecordingsJobs.category.getType("recordings-cleanup")
                        .withName("Recordings cleanup")
                        .getKey(extension.id)

        override fun getDescription(): String =
                "Cleanup of recordings for ${extension.displayName}"

        override fun getTask() = JobRun {
            recordingsCleanupService.cleanup(extension)
        }

        override fun isDisabled(): Boolean = false

    }

}