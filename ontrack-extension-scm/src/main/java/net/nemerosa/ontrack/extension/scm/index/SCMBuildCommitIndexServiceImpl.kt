package net.nemerosa.ontrack.extension.scm.index

import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.changelog.SCMCommit
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import net.nemerosa.ontrack.repository.support.queryForObjectOrNull
import net.nemerosa.ontrack.repository.support.readLocalDateTimeNotNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

@Service
@Transactional
class SCMBuildCommitIndexServiceImpl(
    dataSource: DataSource,
    private val scmDetector: SCMDetector,
    private val entityStore: EntityStore,
    private val structureService: StructureService,
    private val ontrackConfigProperties: OntrackConfigProperties,
) : SCMBuildCommitIndexService, AbstractJdbcRepository(dataSource) {

    override fun clearBuildCommits() {
        @Suppress("SqlWithoutWhere")
        jdbcTemplate?.update(
            "DELETE FROM SCM_BUILD_COMMIT_INDEX"
        )
    }

    override fun indexBuildCommits(project: Project): Int {
        val scm = scmDetector.getSCM(project) ?: return -1
        if (scm !is SCMChangeLogEnabled) return -1

        // Gets the latest build which was indexed for this project
        val lastBuildId = entityStore.findByName<SCMBuildCommitIndexProjectState>(
            entity = project,
            store = SCMBuildCommitIndexProjectState::class.java.name,
            name = project.name,
            type = SCMBuildCommitIndexProjectState::class,
        )?.lastBuildId ?: -1

        // Loops over the builds starting from this latest build ID
        var count = 0
        var lastIndexedBuildId: Int = -1
        namedParameterJdbcTemplate?.query(
            """
                SELECT bd.ID
                FROM BUILDS bd
                INNER JOIN BRANCHES b ON b.ID = bd.BRANCHID
                INNER JOIN PROJECTS p ON p.ID = b.PROJECTID
                WHERE p.ID = :projectId
                AND bd.ID > :lastBuildId
                ORDER BY bd.ID
            """,
            mapOf(
                "projectId" to project.id(),
                "lastBuildId" to lastBuildId,
            )
        ) { rs, _ -> rs.getInt("ID") }
            ?.forEach { buildId ->
                val build = structureService.getBuild(ID.of(buildId))
                val commit = scm.getBuildCommit(build)
                if (commit != null) {
                    val indexedCommit = getIndexedCommit(buildId)
                    if (indexedCommit == null) {
                        val scmCommit = scm.getCommit(commit)
                        if (scmCommit != null) {
                            indexCommit(buildId, scmCommit)
                            // OK
                            lastIndexedBuildId = buildId
                            count++
                        }
                    }
                }
            }

        // Updates the state
        if (lastIndexedBuildId > 0 && lastIndexedBuildId != lastBuildId) {
            entityStore.store(
                entity = project,
                store = SCMBuildCommitIndexProjectState::class.java.name,
                entityStoreRecord = SCMBuildCommitIndexProjectState(
                    name = project.name,
                    lastBuildId = lastIndexedBuildId,
                )
            )
        }

        // OK
        return count
    }

    override fun getBuildCommit(build: Build): SCMBuildCommitIndexData? =
        getIndexedCommit(build.id())

    private fun getIndexedCommit(buildId: Int): SCMBuildCommitIndexData? =
        namedParameterJdbcTemplate?.queryForObjectOrNull(
            sql = """
                SELECT *
                FROM SCM_BUILD_COMMIT_INDEX
                WHERE BUILD_ID = :buildId
            """,
            params = mapOf(
                "buildId" to buildId,
            ),
            rowMapper = { rs, _ ->
                SCMBuildCommitIndexData(
                    buildId = rs.getInt("BUILD_ID"),
                    commitId = rs.getString("COMMIT_ID"),
                    commitTimestamp = rs.readLocalDateTimeNotNull("COMMIT_TIMESTAMP"),
                    commitData = readJson(rs, "COMMIT_DATA"),
                )
            }
        )

    override fun indexBuildCommit(build: Build, commit: String?) {

        /**
         * Not indexing when configurations are disabled.
         */
        if (!ontrackConfigProperties.configurationTest) {
            return
        }

        val scm = scmDetector.getSCM(build.project)
        if (scm !is SCMChangeLogEnabled) return
        val existingCommit = scm.getBuildCommit(build) ?: return
        if (commit != null && existingCommit != commit) return
        val scmCommit = scm.getCommit(existingCommit) ?: return

        indexCommit(build.id(), scmCommit)
    }

    private fun indexCommit(buildId: Int, scmCommit: SCMCommit) {
        namedParameterJdbcTemplate?.update(
            """
                INSERT INTO SCM_BUILD_COMMIT_INDEX(BUILD_ID, COMMIT_ID, COMMIT_TIMESTAMP, COMMIT_DATA)
                VALUES (:buildId, :commitId, :commitTimestamp, CAST(:commitData AS JSONB))
                ON CONFLICT (BUILD_ID, COMMIT_ID) DO 
                UPDATE SET COMMIT_TIMESTAMP = :commitTimestamp, COMMIT_DATA = CAST(:commitData AS JSONB)
            """,
            mapOf(
                "buildId" to buildId,
                "commitId" to scmCommit.id,
                "commitTimestamp" to dateTimeForDB(scmCommit.timestamp),
                "commitData" to writeJson(scmCommit),
            )
        )
    }

    override fun findEarliestBuildAfterCommit(
        branch: Branch,
        commit: String
    ): Build? {
        val scm = scmDetector.getSCM(branch.project) ?: return null
        if (scm !is SCMChangeLogEnabled) return null
        val scmCommit = scm.getCommit(commit) ?: return null
        val buildId = getFirstItem(
            """
                SELECT i.BUILD_ID
                FROM SCM_BUILD_COMMIT_INDEX i
                INNER JOIN BUILDS b ON b.ID = i.BUILD_ID
                WHERE b.BRANCHID = :branchId
                AND i.COMMIT_TIMESTAMP >= :commitTimestamp
                ORDER BY i.BUILD_ID ASC
                LIMIT 1
            """,
            mapOf(
                "branchId" to branch.id(),
                "commitTimestamp" to dateTimeForDB(scmCommit.timestamp),
            ),
            Int::class.java,
        )
        return buildId?.let { structureService.getBuild(ID.of(it)) }
    }

    private data class SCMBuildCommitIndexProjectState(
        override val name: String,
        val lastBuildId: Int,
    ) : EntityStoreRecord

}