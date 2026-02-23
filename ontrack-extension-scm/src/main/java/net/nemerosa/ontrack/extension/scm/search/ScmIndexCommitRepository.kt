package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import net.nemerosa.ontrack.repository.support.readLocalDateTimeNotNull
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class ScmIndexCommitRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource) {

    fun save(project: Project, scmIndexCommit: ScmIndexCommit) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO SCM_INDEX_COMMITS (COMMIT_ID, PROJECT_ID, COMMIT_SHORT, COMMIT_TIMESTAMP, MESSAGE)
                VALUES (:commitId, :projectId, :commitShort, :commitTimestamp, :message)
                ON CONFLICT (COMMIT_ID, PROJECT_ID) DO NOTHING
            """.trimIndent(),
            mapOf(
                "commitId" to scmIndexCommit.commitId,
                "projectId" to project.id(),
                "commitShort" to scmIndexCommit.commitShort,
                "commitTimestamp" to dateTimeForDB(scmIndexCommit.commitTimestamp),
                "message" to scmIndexCommit.message.take(512),
            )
        )
    }

    /**
     * Gets a paginated list of commits for a project, from the newest to the oldest.
     */
    fun getCommits(project: Project, offset: Int, size: Int): PaginatedList<ScmIndexCommit> {
        val count = namedParameterJdbcTemplate!!.queryForObject(
            """
                SELECT COUNT(*)
                FROM SCM_INDEX_COMMITS
                WHERE PROJECT_ID = :projectId
            """.trimIndent(),
            mapOf(
                "projectId" to project.id(),
            ),
            Int::class.java
        ) ?: 0

        val commits = namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM SCM_INDEX_COMMITS
                WHERE PROJECT_ID = :projectId
                OFFSET :offset LIMIT :size
            """.trimIndent(),
            mapOf(
                "projectId" to project.id(),
                "offset" to offset,
                "size" to size,
            )
        ) { rs, _ ->
            toCommit(rs)
        }

        return PaginatedList.create(
            items = commits,
            offset = offset,
            pageSize = size,
            total = count,
        )
    }

    private fun toCommit(rs: ResultSet): ScmIndexCommit = ScmIndexCommit(
        commitId = rs.getString("COMMIT_ID").trim(),
        commitShort = rs.getString("COMMIT_SHORT").trim(),
        commitTimestamp = rs.readLocalDateTimeNotNull("COMMIT_TIMESTAMP"),
        message = rs.getString("MESSAGE"),
    )

    fun findLastCommit(id: Int, key: String): ScmIndexCommit? {
        return namedParameterJdbcTemplate!!.query(
            """
                SELECT C.*
                FROM SCM_INDEX_COMMIT_ISSUES L
                INNER JOIN SCM_INDEX_COMMITS C ON L.COMMIT_ID = C.COMMIT_ID
                WHERE L.PROJECT_ID = :projectId AND L.ISSUE_KEY = :issueKey
                ORDER BY C.COMMIT_TIMESTAMP DESC
                LIMIT 1
            """.trimIndent(),
            mapOf(
                "projectId" to id,
                "issueKey" to key
            )
        ) { rs, _ ->
            toCommit(rs)
        }.firstOrNull()
    }

}