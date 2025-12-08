package net.nemerosa.ontrack.extension.av.scheduler

import net.nemerosa.ontrack.common.Time
import java.time.LocalDateTime

/**
 * Computing schedules
 */
interface ScheduleService {

    /**
     * Given a CRON expression, computes the next execution time.
     */
    fun nextExecutionTime(cron: String, now: LocalDateTime = Time.now): LocalDateTime

}