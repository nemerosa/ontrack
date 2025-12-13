package net.nemerosa.ontrack.extension.av.scheduler

import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class ScheduleServiceImpl : ScheduleService {

    override fun nextExecutionTime(cron: String, now: LocalDateTime): LocalDateTime {
        val cronExpression = CronExpression.parse(cron)
        val zonedNow = now.atZone(ZoneId.of("UTC"))
        val nextExecution = cronExpression.next(zonedNow)
            ?: error("No next execution time for cron expression: $cron")
        return nextExecution.toLocalDateTime()
    }

}