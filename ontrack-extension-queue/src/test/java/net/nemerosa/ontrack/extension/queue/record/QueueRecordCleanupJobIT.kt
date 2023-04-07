package net.nemerosa.ontrack.extension.queue.record

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.extension.queue.settings.QueueSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class QueueRecordCleanupJobIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var queueRecordStore: QueueRecordStore

    @Autowired
    private lateinit var queueRecordCleanupJob: QueueRecordCleanupJob

    private val ref = Time.now()

    /**
     * Testing the deletion of different queue records.
     *
     * ```
     * * running record to be deleted
     * * non-running record to be deleted
     * * --- cleanup cutoff
     * * running record to be kept
     * * non-running record to be deleted
     * * --- retention cutoff
     * * running record to be kept
     * * non-running record to be kept
     * ```
     */
    @Test
    fun `Cleanup of both running and non running records`() {
        asAdmin {
            withCleanSettings<QueueSettings> {
                settingsManagerService.saveSettings(
                        QueueSettings(
                                recordRetentionDuration = Duration.ofDays(10),
                                recordCleanupDuration = Duration.ofDays(10),
                        )
                )
                // Records before the cleanup cutoff (10 + 10)
                val beforeCleanupRunning = createPayload(daysBefore = 25, running = true)
                val beforeCleanupNotRunning = createPayload(daysBefore = 25, running = false)
                val beforeRetentionRunning = createPayload(daysBefore = 15, running = true)
                val beforeRetentionNotRunning = createPayload(daysBefore = 15, running = false)
                val afterRetentionRunning = createPayload(daysBefore = 5, running = true)
                val afterRetentionNotRunning = createPayload(daysBefore = 5, running = false)
                // Running the cleanup
                queueRecordCleanupJob.startingJobs.first().job.task.run(JobRunListener.out())
                // Checking the state of records
                assertRecordNotPresent(beforeCleanupRunning, "Running record before the cleanup cutoff must be deleted")
                assertRecordNotPresent(beforeCleanupNotRunning, "Not running record before the cleanup cutoff must be deleted")
                assertRecordPresent(beforeRetentionRunning, "Running record before the retention cutoff must NOT be deleted")
                assertRecordNotPresent(beforeRetentionNotRunning, "Not running record before the retention cutoff must be deleted")
                assertRecordPresent(afterRetentionRunning, "Running record after the retention cutoff must NOT be deleted")
                assertRecordPresent(afterRetentionNotRunning, "Not running record after the retention cutoff must NOT be deleted")
            }
        }
    }

    private fun assertRecordNotPresent(id: String, message: String) {
        val record = queueRecordStore.findByQueuePayloadID(id)
        assertNull(record, message)
    }

    private fun assertRecordPresent(id: String, message: String) {
        val record = queueRecordStore.findByQueuePayloadID(id)
        assertNotNull(record, message)
    }

    private fun createPayload(
            daysBefore: Int?,
            running: Boolean,
    ): String {
        val record = QueueRecord(
                state = if (running) {
                    QueueRecordState.PROCESSING
                } else {
                    QueueRecordState.COMPLETED
                },
                queuePayload = QueuePayload(
                        id = UUID.randomUUID().toString(),
                        processor = "test",
                        body = TextNode("Some payload"),
                ),
                startTime = daysBefore?.let { ref.minusDays(it.toLong()) } ?: ref,
                endTime = if (running) {
                    null
                } else if (daysBefore != null) {
                    ref.minusDays(daysBefore.toLong() - 1)
                } else {
                    ref
                },
                routingKey = null,
                queueName = null,
                actualPayload = TextNode("Some payload"),
                exception = null,
                history = emptyList(),
        )

        queueRecordStore.save(record)

        return record.queuePayload.id
    }

}