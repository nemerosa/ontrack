package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JobConfigIT : AbstractServiceTestSupport() {

    class TestJob(
            private val key: String,
    ) : Job {

        override fun getKey(): JobKey {
            return JobCategory.of("test-category").getType("test-type").getKey(key)
        }

        override fun getTask(): JobRun {
            return JobRun { runListener ->
                runListener.message("Running the test job...")
            }
        }

        override fun getDescription(): String = "Test job"

        override fun isDisabled(): Boolean = false
    }

    @Autowired
    private lateinit var jobScheduler: JobScheduler

    @Autowired
    private lateinit var jobListener: JobListener

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `The paused status of a job is persisted`() {
        // Test job
        val job = TestJob("test-job")
        // Checks the job key
        assertEquals("[test-category][test-type][test-job]", job.key.toString())
        // Schedules the test job
        jobScheduler.schedule(job, Schedule.everyMinutes(60).after(60))
        // Checks it's not paused
        assertFalse(
                settingsRepository.getBoolean(
                        JobListener::class.java,
                        "[test-category][test-type][test-job]",
                        false
                ),
                "Job must not be paused"
        )
        assertFalse(
                jobListener.isPausedAtStartup(job.key),
                "Job must not be paused in listener"
        )
        // Pauses the job
        jobScheduler.pause(job.key)
        // Checks it's paused
        assertTrue(
                settingsRepository.getBoolean(
                        JobListener::class.java,
                        "[test-category][test-type][test-job]",
                        false
                ),
                "Job must be paused"
        )
        assertTrue(jobListener.isPausedAtStartup(job.key), "Job must be paused in listener")
    }

    @Test
    fun `Job paused at startup`() {
        // Test job
        val job = TestJob("test-2")
        // Checks the job key
        assertEquals("[test-category][test-type][test-2]", job.key.toString())
        // Pauses it at startup
        settingsRepository.setBoolean(
                JobListener::class.java,
                "[test-category][test-type][test-2]",
                true
        )
        // Checks it's paused
        assertTrue(
                settingsRepository.getBoolean(
                        JobListener::class.java,
                        "[test-category][test-type][test-2]",
                        false
                ),
                "Job must be paused"
        )
        assertTrue(
                jobListener.isPausedAtStartup(job.key),
                "Job must be paused in listener"
        )
        // Schedules the test job
        jobScheduler.schedule(job, Schedule.everyMinutes(60).after(60))
        // Checks it's paused
        val status = jobScheduler.getJobStatus(job.key).getOrNull()
        assertNotNull(status, "The job has a status") {
            assertTrue(it.isPaused, "The job must be paused")
        }
    }

}
