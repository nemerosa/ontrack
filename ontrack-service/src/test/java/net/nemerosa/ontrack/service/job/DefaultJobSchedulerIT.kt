package net.nemerosa.ontrack.service.job

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DefaultJobSchedulerIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var jobScheduler: JobScheduler

    @Test
    fun `Job cron schedule`() {
        var count = 0
        val task = object : Job {
            override fun getKey(): JobKey = JobCategory.of("test").getType("jobs").getKey("cron")

            override fun getTask() = JobRun {
                println("Cron (${++count})")
            }

            override fun getDescription(): String = "Cron job"

            override fun isDisabled(): Boolean = false
        }
        jobScheduler.schedule(task, Schedule.cron("* * * * * *")) // Every second
        runBlocking {
            delay(4_000) // Waiting 3 seconds
        }
        // Checks that the job has run
        val stopCount = count
        assertTrue(stopCount >= 2, "The job has run at least twice (but has run ${stopCount} time(s).")
        // Unscheduling the job
        jobScheduler.unschedule(task.key)
        // Waiting a bit
        runBlocking {
            delay(1_500) // Waiting 1.5 seconds
        }
        // Check the count has not changed
        assertEquals(stopCount, count, "The job has stopped running")
    }

    @Test
    fun `Job timeout`() {
        var count = 0
        var completed = false
        val longRunningJob = object : Job {
            override fun getKey(): JobKey = JobCategory.of("test").getType("jobs").getKey("timeout")

            override fun getTask() = JobRun {
                runBlocking {
                    repeat(30) {
                        count++
                        println("Task ($it)")
                        delay(100)
                    }
                    println("Done")
                    completed = true
                }
            }

            override fun getDescription(): String = "Long running job"

            override fun isDisabled(): Boolean = false

            override fun getTimeout() = Duration.ofMillis(500)
        }
        jobScheduler.schedule(longRunningJob, Schedule.NONE)
        jobScheduler.fireImmediately(longRunningJob.key)
        runBlocking {
            delay(1000)
        }
        val stopped = jobScheduler.checkForTimeouts()
        runBlocking {
            delay(500)
        }
        assertTrue(count > 0, "At least some tasks had started to run")
        assertFalse(completed, "The job was interrupted")
        assertTrue(stopped > 0, "At least one job was stopped")

        // Gets the statuses and controls that the timeout counter has increased for this job
        val status = jobScheduler.jobStatuses.find { it.key == longRunningJob.key }
        assertNotNull(status, "Getting the status of the job") {
            assertEquals(1, it.lastTimeoutCount, "One timeout event")
            assertTrue(it.isTimeout, "Marked as in timeout")
        }

    }

}