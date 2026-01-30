package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.job.JobHistoryItem
import java.time.LocalDateTime

interface JobHistoryRepository {

    fun record(item: JobHistoryItem): Int
    fun findById(itemId: Int): JobHistoryItem?
    fun getHistory(
        jobCategory: String,
        jobType: String,
        jobKey: String,
        from: LocalDateTime,
        to: LocalDateTime,
        skipErrors: Boolean
    ): List<JobHistoryItem>

}