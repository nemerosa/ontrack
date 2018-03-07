package net.nemerosa.ontrack.job.support

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.test.assertNotPresent
import net.nemerosa.ontrack.test.assertPresent
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JobSchedulingTest {

    private lateinit var schedulerPool: SynchronousScheduledExecutorService
    private lateinit var jobPool: SynchronousScheduledExecutorService

    @Before
    fun before() {
        schedulerPool = SynchronousScheduledExecutorService()
        jobPool = SynchronousScheduledExecutorService()
    }

    @After
    fun after() {
        schedulerPool.shutdownNow()
        jobPool.shutdownNow()
    }

    @Test
    fun schedule() {
        scheduler {
            job {
                tick_seconds(3)
                assertEquals(4, count)
            }
        }
    }

    @Test
    fun `Schedule with wait at startup`() {
        scheduler {
            job(Schedule.EVERY_SECOND.after(1)) {
                tick_seconds(3)
                assertEquals(3, count)
            }
        }
    }

    @Test
    fun scheduler_paused_at_startup() {
        scheduler(initiallyPaused = true) {
            job {
                tick_seconds(3)
                assertEquals(0, count) // Job did not run
                // Resumes the execution and waits
                scheduler.resume()
                tick_seconds(2)
                // The job has run
                assertEquals(2, count)
            }
        }
    }

    @Test
    fun reschedule() {
        scheduler {
            job {
                // Checks it has run
                tick_seconds(2)
                val currentCount = count
                assertEquals(3, currentCount)
                // Then every minute
                schedule(this, Schedule(1, 1, TimeUnit.MINUTES))
                // Checks after three more seconds than the count has not moved
                tick_seconds(3)
                assertEquals(currentCount, count)
            }
        }
    }

    @Test
    fun fire_immediately() {
        scheduler {
            job(Schedule.EVERY_MINUTE.after(10)) {
                // Not fired, even after 10 seconds
                tick_seconds(10)
                assertEquals(0, count)
                // Fires immediately and waits for the result
                fireImmediatelyRequired(this)
                jobPool.runUntilIdle()
                assertEquals(1, count)

            }
        }
    }

    @Test
    fun fire_immediately_in_concurrency() {
        scheduler {
            job {
                // Job is started and scheduled
                schedulerPool.runUntilIdle()
                // ... but nothing has happened yet
                assertEquals(0, count)
                // Checks its status
                val jobStatus = scheduler.getJobStatus(key)
                assertPresent(jobStatus) {
                    assertTrue(it.isRunning)
                }
                // Fires immediately and waits for the result
                assertNotPresent(
                        fireImmediately(this),
                        "Job is not fired because already running"
                )
                // The job is already running, count is still 0
                assertEquals(0, count)
                // Waits until completion
                jobPool.runUntilIdle()
                assertEquals(1, count)
            }
        }
    }

    @Test
    fun `Removing a running job`() {
        scheduler {
            val job = job(Schedule.EVERY_SECOND)
            // After some seconds, the job keeps running
            tick_seconds(3)
            assertEquals(4, job.count)
            // Now, removes the job
            unschedule(job)
            // Waits a bit, and checks the job has stopped running
            tick_seconds(3)
            assertEquals(4, job.count)
        }
    }

    @Test
    fun `Disabled job cannot be fired`() {
        scheduler {
            val job = ConfigurableJob()
            job.pause()
            // Initially every second
            schedule(job, Schedule.EVERY_SECOND)
            tick_seconds(2)
            // After a few seconds, the count has NOT moved
            assertEquals(0, job.count)
            // Forcing the run
            val future = fireImmediately(job)
            assertNotPresent(future, "Job not fired")
            // ... to not avail
            assertEquals(0, job.count)
        }
    }

    /**
     * Runs the scheduler for {count} seconds with intervals of 1/2 seconds.
     */
    private fun tick_seconds(count: Int) {
        repeat(count * 2) {
            schedulerPool.tick(500, TimeUnit.MILLISECONDS)
            jobPool.runUntilIdle()
        }
    }

    private fun scheduler(initiallyPaused: Boolean = false, code: JobSchedulerContext.() -> Unit) {
        val jobScheduler = createJobScheduler(initiallyPaused)
        JobSchedulerContext(jobScheduler).code()
    }

    private fun createJobScheduler(initiallyPaused: Boolean): JobScheduler {
        return DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                schedulerPool,
                NOPJobListener.INSTANCE,
                initiallyPaused,
                BiFunction { _, _ -> jobPool },
                false,
                1.0
        )
    }

    inner class JobSchedulerContext(
            val scheduler: JobScheduler
    ) {
        fun job(schedule: Schedule): ConfigurableJob {
            val job = ConfigurableJob()
            scheduler.schedule(job, schedule)
            return job
        }

        fun job(schedule: Schedule = Schedule.EVERY_SECOND, code: ConfigurableJob.() -> Unit) {
            val job = job(schedule)
            job.code()
        }

        fun unschedule(job: Job) {
            scheduler.unschedule(job.key)
        }

        fun <T : Job> schedule(job: T, schedule: Schedule): T {
            scheduler.schedule(job, schedule)
            return job
        }

        fun fireImmediately(job: Job): Optional<Future<*>> = scheduler.fireImmediately(job.key)

        fun fireImmediatelyRequired(job: Job) {
            fireImmediately(job).orElseThrow { IllegalStateException("No future being returned.") }
        }
    }


}