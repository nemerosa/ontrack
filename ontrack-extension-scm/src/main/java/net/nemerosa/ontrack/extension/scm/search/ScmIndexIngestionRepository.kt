package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.scm.changelog.SCMCommit
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import net.nemerosa.ontrack.repository.support.readLocalDateTime
import net.nemerosa.ontrack.repository.support.readLocalDateTimeNotNull
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ScmIndexIngestionRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource) {

    fun getLastIngestion(projectId: Int): ScmIndexIngestion? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM SCM_INDEX_INGESTION
                WHERE PROJECT_ID = :projectId
            """.trimIndent(),
            mapOf(
                "projectId" to projectId
            )
        ) { rs, _ ->
            ScmIndexIngestion(
                lastIngestedAt = rs.readLocalDateTimeNotNull("LAST_INGESTED_AT"),
                lastCommit = rs.getString("LAST_COMMIT")?.trim(),
                lastCommitTimestamp = rs.readLocalDateTime("LAST_COMMIT_TIMESTAMP")
            )
        }.firstOrNull()

    fun saveLastIngestion(projectId: Int, lastIngestion: ScmIndexIngestion?, lastScmCommit: SCMCommit?) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO SCM_INDEX_INGESTION (PROJECT_ID, LAST_INGESTED_AT, LAST_COMMIT, LAST_COMMIT_TIMESTAMP)
                VALUES (:projectId, :lastIngestedAt, :lastCommit, :lastCommitTimestamp)
                ON CONFLICT (PROJECT_ID) DO UPDATE SET LAST_INGESTED_AT = :lastIngestedAt, LAST_COMMIT = :lastCommit, LAST_COMMIT_TIMESTAMP = :lastCommitTimestamp
            """.trimIndent(),
            mapOf(
                "projectId" to projectId,
                "lastCommit" to (lastScmCommit?.id ?: lastIngestion?.lastCommit),
                "lastCommitTimestamp" to dateTimeForDB(lastScmCommit?.timestamp ?: lastIngestion?.lastCommitTimestamp),
                "lastIngestedAt" to dateTimeForDB(Time.now),
            )
        )
    }
}