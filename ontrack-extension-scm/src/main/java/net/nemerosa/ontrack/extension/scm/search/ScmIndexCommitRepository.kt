package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
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

}