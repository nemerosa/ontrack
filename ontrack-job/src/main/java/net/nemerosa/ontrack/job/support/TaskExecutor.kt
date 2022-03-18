package net.nemerosa.ontrack.job.support

import java.time.Duration
import java.util.concurrent.ScheduledFuture

/**
 * Internal facade, used for runtime & tests, used to schedule actual tasks.
 */
interface TaskExecutor {

    fun scheduleAtFixedDelay(
        task: Runnable,
        initialDelay: Duration,
        delay: Duration,
    ): ScheduledFuture<*>

    fun scheduleCron(task: Runnable, cron: String): ScheduledFuture<*>?

}