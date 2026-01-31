package net.nemerosa.ontrack.model.job

import net.nemerosa.ontrack.job.JobKey
import net.nemerosa.ontrack.job.support.JobHistoryRecorder
import java.time.Duration
import java.time.LocalDateTime

interface JobHistoryService : JobHistoryRecorder {

    fun findById(itemId: Int): JobHistoryItem?

    fun getHistory(jobKey: JobKey, from: LocalDateTime, to: LocalDateTime, skipErrors: Boolean): List<JobHistoryItem>

    fun getHistogram(
        jobKey: JobKey,
        from: LocalDateTime,
        to: LocalDateTime,
        interval: Duration,
        skipErrors: Boolean
    ): JobHistogram

    fun cleanup()

}