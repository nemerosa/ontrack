package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.extension.hook.HookResponse
import net.nemerosa.ontrack.extension.hook.HookResponseType
import net.nemerosa.ontrack.extension.hook.settings.HookSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HookRecordCleanupJobIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var hookRecordStore: HookRecordStore

    @Autowired
    private lateinit var hookRecordCleanupJob: HookRecordCleanupJob

    private val ref = Time.now()

    /**
     * Testing the deletion of different hook records.
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
            withCleanSettings<HookSettings> {
                settingsManagerService.saveSettings(
                        HookSettings(
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
                hookRecordCleanupJob.startingJobs.first().job.task.run(JobRunListener.out())
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
        val record = hookRecordStore.findById(id)
        assertNull(record, message)
    }

    private fun assertRecordPresent(id: String, message: String) {
        val record = hookRecordStore.findById(id)
        assertNotNull(record, message)
    }

    private fun createPayload(
            daysBefore: Int?,
            running: Boolean,
    ): String {
        val record = HookRecord(
                id = UUID.randomUUID().toString(),
                hook = "test",
                request = HookRequest(
                        body = "Some body",
                        parameters = emptyMap(),
                        headers = emptyMap(),
                ),
                startTime = daysBefore?.let { ref.minusDays(it.toLong()) } ?: ref,
                state = if (running) {
                    HookRecordState.RECEIVED
                } else {
                    HookRecordState.SUCCESS
                },
                message = null,
                exception = null,
                endTime = if (running) {
                    null
                } else if (daysBefore != null) {
                    ref.minusDays(daysBefore.toLong() - 1)
                } else {
                    ref
                },
                response = if (running) {
                    null
                } else {
                    HookResponse(
                            type = HookResponseType.PROCESSING,
                            info = null,
                    )
                }
        )

        hookRecordStore.save(record)

        return record.id
    }

}