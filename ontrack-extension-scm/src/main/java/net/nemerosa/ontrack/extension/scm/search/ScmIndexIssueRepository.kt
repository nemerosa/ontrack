package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ScmIndexIssueRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource) {

    fun index(project: Project, keys: Set<String>, commitId: String) {
        for (key in keys) {
            namedParameterJdbcTemplate!!.update(
                """
                    INSERT INTO SCM_INDEX_ISSUES (PROJECT_ID, ISSUE_KEY)
                    VALUES (:projectId, :issueKey)
                    ON CONFLICT (PROJECT_ID, ISSUE_KEY) DO NOTHING
                """.trimIndent(),
                mapOf(
                    "projectId" to project.id(),
                    "issueKey" to key,
                )
            )
            namedParameterJdbcTemplate!!.update(
                """
                    INSERT INTO SCM_INDEX_COMMIT_ISSUES (PROJECT_ID, COMMIT_ID, ISSUE_KEY)
                    VALUES (:projectId, :commitId, :issueKey)
                    ON CONFLICT (PROJECT_ID, COMMIT_ID, ISSUE_KEY) DO NOTHING
                """.trimIndent(),
                mapOf(
                    "projectId" to project.id(),
                    "commitId" to commitId,
                    "issueKey" to key,
                )
            )
        }
    }

    fun findIssues(key: String): List<ScmIndexIssue> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT PROJECT_ID
                FROM SCM_INDEX_ISSUES
                WHERE ISSUE_KEY = :key
            """.trimIndent(),
            mapOf(
                "key" to key
            )
        ) { rs, _ ->
            ScmIndexIssue(
                key = key,
                projectId = rs.getInt("PROJECT_ID"),
            )
        }

}