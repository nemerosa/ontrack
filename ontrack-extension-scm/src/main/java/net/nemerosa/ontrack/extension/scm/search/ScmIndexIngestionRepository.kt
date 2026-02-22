package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
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
                lastCommit = rs.getString("LAST_COMMIT"),
                lastCommitTimestamp = rs.readLocalDateTimeNotNull("LAST_COMMIT_TIMESTAMP")
            )
        }.firstOrNull()
}