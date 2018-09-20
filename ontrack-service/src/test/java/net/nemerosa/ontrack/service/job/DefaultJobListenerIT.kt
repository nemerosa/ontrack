package net.nemerosa.ontrack.service.job

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.JobCategory
import net.nemerosa.ontrack.job.JobStatus
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.support.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class DefaultJobListenerIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var logService: ApplicationLogService
    @Autowired
    private lateinit var meterRegistry: MeterRegistry
    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun onJobError() {
        val jobListener = DefaultJobListener(logService, meterRegistry, settingsRepository)
        val jobStatus = JobStatus(
                id = 1,
                key = JobCategory.of("test").getType("test").getKey("1"),
                schedule = Schedule.EVERY_MINUTE,
                actualSchedule = Schedule.EVERY_MINUTE,
                description = "Test job",
                isDisabled = false,
                isPaused = false,
                isRunning = false,
                isValid = true,
                lastError = null,
                lastErrorCount = 0,
                lastRunDate = null,
                lastRunDurationMs = 0,
                nextRunDate = null,
                progress = null,
                runCount = 0
        )
        jobListener.onJobError(jobStatus, RuntimeException("Test exception"))
        // Checks the error has been logged
        val entries = asAdmin().call {
            logService.getLogEntries(ApplicationLogEntryFilter.none(), Page(0, 1))
        }
        assertEquals(1, entries.size)
        val entry: ApplicationLogEntry = entries[0]
        assertEquals("Test job", entry.information)
    }
}