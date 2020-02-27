package net.nemerosa.ontrack.job.support

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.test.assertPresent
import org.junit.After
import org.junit.Before
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction

abstract class AbstractJobTest {

    protected lateinit var schedulerPool: SynchronousScheduledExecutorService
    protected lateinit var jobPool: SynchronousScheduledExecutorService

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

    /**
     * Runs the scheduler for {count} seconds with intervals of 1/2 seconds.
     */
    protected fun tick_seconds(count: Int) {
        repeat(count * 2) {
            schedulerPool.tick(500, TimeUnit.MILLISECONDS)
            jobPool.runUntilIdle()
        }
    }

    protected fun scheduler(initiallyPaused: Boolean = false, code: JobSchedulerContext.() -> Unit) {
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

        fun <T : Job> schedule(job: T, schedule: Schedule = Schedule.EVERY_SECOND): T {
            scheduler.schedule(job, schedule)
            return job
        }

        fun fireImmediately(job: Job): Optional<CompletableFuture<*>> = scheduler.fireImmediately(job.key)

        fun fireImmediatelyRequired(job: Job) {
            fireImmediately(job).orElseThrow { IllegalStateException("No future being returned.") }
        }

        fun status(key: JobKey, code: JobStatus.() -> Unit) {
            val jobStatus = scheduler.getJobStatus(key)
            assertPresent(jobStatus) {
                it.code()
            }
        }
    }


}