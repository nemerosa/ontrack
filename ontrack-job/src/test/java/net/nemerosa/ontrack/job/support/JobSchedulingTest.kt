package net.nemerosa.ontrack.job.support

import net.nemerosa.ontrack.job.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import kotlin.test.assertEquals

class JobSchedulingTest {

    private lateinit var schedulerPool: SynchronousScheduledExecutorService
    private lateinit var jobPool: SynchronousScheduledExecutorService

    private val noFutureException = { IllegalStateException("No future being returned.") }

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
    fun `Removing a running job`() {
        scheduler {
            val job = job(Schedule.EVERY_SECOND)
            // After some seconds, the job keeps running
            tick_seconds(3)
            assertEquals(4, job.count.toLong())
            // Now, removes the job
            unschedule(job)
            // Waits a bit, and checks the job has stopped running
            tick_seconds(3)
            assertEquals(4, job.count.toLong())
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

    private fun scheduler(code: JobSchedulerContext.() -> Unit) {
        val jobScheduler = createJobScheduler()
        JobSchedulerContext(jobScheduler).code()
    }

    private fun createJobScheduler(): JobScheduler {
        return createJobScheduler(false)
    }

    private fun createJobScheduler(initiallyPaused: Boolean): JobScheduler {
        return DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                schedulerPool,
                NOPJobListener.INSTANCE,
                initiallyPaused,
                BiFunction { pool, job -> jobPool },
                false,
                1.0
        )
    }

    inner class JobSchedulerContext(
            private val scheduler: JobScheduler
    ) {
        fun job(schedule: Schedule): TestJob {
            val job = TestJob.of()
            scheduler.schedule(job, schedule)
            return job
        }

        fun unschedule(job: Job) {
            scheduler.unschedule(job.key)
        }
    }


}