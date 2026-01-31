package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.job.JobHistoryItem
import net.nemerosa.ontrack.model.job.JobHistoryItemStatus
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import net.nemerosa.ontrack.repository.support.readLocalDateTimeNotNull
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import javax.sql.DataSource

@Repository
class JobHistoryJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), JobHistoryRepository {

    override fun record(item: JobHistoryItem): Int {
        val keyHolder = GeneratedKeyHolder()
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO JOB_HISTORY(JOB_CATEGORY, JOB_TYPE, JOB_KEY, STARTED_AT, ENDED_AT, STATUS, MESSAGE)
                VALUES (:jobCategory, :jobType, :jobKey, :startedAt, :endedAt, :status, :message)
            """.trimIndent(),
            MapSqlParameterSource(
                mapOf(
                    "jobCategory" to item.jobCategory,
                    "jobType" to item.jobType,
                    "jobKey" to item.jobKey,
                    "startedAt" to dateTimeForDB(item.startedAt),
                    "endedAt" to dateTimeForDB(item.endedAt),
                    "status" to item.status.name,
                    "message" to item.message,
                )
            ), keyHolder,
            KEYS
        )
        return keyHolder.key!!.toInt()
    }

    override fun findById(itemId: Int): JobHistoryItem? {
        return getFirstItem(
            """
                SELECT * FROM JOB_HISTORY WHERE ID = :id
            """.trimIndent(),
            params("id", itemId)
        ) { rs, _ ->
            readItem(rs)
        }
    }

    override fun getHistory(
        jobCategory: String,
        jobType: String,
        jobKey: String,
        from: LocalDateTime,
        to: LocalDateTime,
        skipErrors: Boolean
    ): List<JobHistoryItem> {

        val params = mutableMapOf<String, Any?>()
        params["jobCategory"] = jobCategory
        params["jobType"] = jobType
        params["jobKey"] = jobKey
        params["from"] = dateTimeForDB(from)
        params["to"] = dateTimeForDB(to)

        var query = """
                SELECT * FROM JOB_HISTORY
                WHERE JOB_CATEGORY = :jobCategory 
                AND JOB_TYPE = :jobType 
                AND JOB_KEY = :jobKey 
                AND STARTED_AT >= :from 
                AND STARTED_AT <= :to
            """

        if (skipErrors) {
            query += " AND STATUS <> 'ERROR'"
        }

        return namedParameterJdbcTemplate!!.query(
            query,
            params,
        ) { rs, _ ->
            readItem(rs)
        }
    }

    override fun cleanup(cutoffTime: LocalDateTime) {
        namedParameterJdbcTemplate!!.update(
            """
                DELETE FROM JOB_HISTORY WHERE STARTED_AT < :cutoffTime
            """.trimIndent(),
            params("cutoffTime", dateTimeForDB(cutoffTime))
        )
    }

    private fun readItem(rs: ResultSet): JobHistoryItem = JobHistoryItem(
        id = rs.getInt("id"),
        jobCategory = rs.getString("job_category"),
        jobType = rs.getString("job_type"),
        jobKey = rs.getString("job_key"),
        startedAt = rs.readLocalDateTimeNotNull("started_at"),
        endedAt = rs.readLocalDateTimeNotNull("ended_at"),
        status = JobHistoryItemStatus.valueOf(rs.getString("status")),
        message = rs.getString("message"),
    )
}