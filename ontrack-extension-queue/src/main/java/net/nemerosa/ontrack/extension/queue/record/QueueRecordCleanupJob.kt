package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueueJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class QueueRecordCleanupJob(
        private val queueRecordCleanupService: QueueRecordCleanupService,
) : JobProvider {

    override fun getStartingJobs() = listOf(
            JobRegistration(
                    createQueueRecordCleanupJob(),
                    Schedule.EVERY_DAY
            )
    )

    private fun createQueueRecordCleanupJob() = object : Job {

        override fun getKey(): JobKey =
                QueueJobs.category
                        .getType("queue-record-cleanup")
                        .withName("Queue record cleanup")
                        .getKey("main")

        override fun getTask() = JobRun { listener ->
            val count = queueRecordCleanupService.cleanup()
            listener.message("Removed $count records")
        }

        override fun getDescription(): String = "Cleanup of old queue message records"

        override fun isDisabled(): Boolean = false

    }

}