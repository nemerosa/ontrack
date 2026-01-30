package net.nemerosa.ontrack.job.support

import net.nemerosa.ontrack.job.JobKey
import java.time.LocalDateTime

interface JobHistoryRecorder {

    fun record(
        jobKey: JobKey,
        startedAt: LocalDateTime,
        endedAt: LocalDateTime,
        error: String?
    ): Int

}