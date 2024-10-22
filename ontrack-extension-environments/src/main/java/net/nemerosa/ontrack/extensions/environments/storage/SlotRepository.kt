package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotPipelineStub
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.BuildJdbcRepositoryAccessor
import net.nemerosa.ontrack.repository.ProjectJdbcRepositoryAccessor
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class SlotRepository(
    dataSource: DataSource,
    private val environmentRepositoryAccessor: EnvironmentRepositoryAccessor,
    private val projectJdbcRepositoryAccessor: ProjectJdbcRepositoryAccessor,
    private val buildJdbcRepositoryAccessor: BuildJdbcRepositoryAccessor,
) : AbstractJdbcRepository(dataSource) {

    fun addSlot(slot: Slot) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO ENV_SLOTS (ID, ENVIRONMENT_ID, PROJECT_ID, QUALIFIER, DESCRIPTION)
                VALUES (:id, :environmentId, :projectId, :qualifier, :description)
                ON CONFLICT (ID) DO UPDATE SET
                    ENVIRONMENT_ID = EXCLUDED.ENVIRONMENT_ID,
                    PROJECT_ID = EXCLUDED.PROJECT_ID,
                    QUALIFIER = EXCLUDED.QUALIFIER,
                    DESCRIPTION = EXCLUDED.DESCRIPTION;
            """,
            mapOf(
                "id" to slot.id,
                "environmentId" to slot.environment.id,
                "projectId" to slot.project.id(),
                "qualifier" to slot.qualifier,
                "description" to slot.description,
            )
        )
    }

    fun findSlotById(id: String): Slot? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOTS
                WHERE ID = :id
            """,
            mapOf("id" to id),
        ) { rs, _ ->
            toSlot(rs)
        }.firstOrNull()

    fun getSlotById(id: String): Slot =
        findSlotById(id) ?: throw SlotIdNotFoundException(id)

    private fun toSlot(rs: ResultSet) = Slot(
        id = rs.getString("ID"),
        environment = environmentRepositoryAccessor.getEnvironmentById(rs.getString("ENVIRONMENT_ID")),
        project = projectJdbcRepositoryAccessor.getProject(ID.of(rs.getInt("PROJECT_ID"))),
        qualifier = rs.getString("QUALIFIER"),
        description = rs.getString("DESCRIPTION"),
    )

    fun findByEnvironmentAndProjectAndQualifier(environment: Environment, project: Project, qualifier: String): Slot? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOTS
                WHERE ENVIRONMENT_ID = :environmentId
                AND PROJECT_ID = :projectId
                AND QUALIFIER = :qualifier
            """,
            mapOf(
                "environmentId" to environment.id,
                "projectId" to project.id(),
                "qualifier" to qualifier,
            ),
        ) { rs, _ ->
            toSlot(rs)
        }.firstOrNull()

    fun findByEnvironment(environment: Environment): List<Slot> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOTS
                WHERE ENVIRONMENT_ID = :environmentId
            """,
            mapOf(
                "environmentId" to environment.id,
            ),
        ) { rs, _ ->
            toSlot(rs)
        }

    fun getEligibleBuilds(
        slot: Slot,
        queries: List<String>,
        params: Map<String, Any?>,
    ): List<Build> {
        var query = """
            SELECT DISTINCT (BD.ID)
            FROM BUILDS BD
                     INNER JOIN BRANCHES B ON BD.BRANCHID = B.ID
                     LEFT JOIN PROMOTION_RUNS PR ON BD.ID = PR.BUILDID
                     LEFT JOIN PROMOTION_LEVELS PL ON B.ID = PL.BRANCHID
            WHERE B.PROJECTID = :projectId
            AND B.DISABLED = FALSE
        """.trimIndent()

        query += queries.joinToString("") { " AND ($it)" }

        val parameters = params.toMutableMap()
        parameters["projectId"] = slot.project.id()

        // Order
        query += " ORDER BY BD.ID DESC"

        return namedParameterJdbcTemplate!!.query(
            query,
            parameters,
        ) { rs, _ ->
            val id = rs.getInt("ID")
            buildJdbcRepositoryAccessor.getBuild(ID.of(id))
        }
    }

    fun findSlotPipelineStubsByBuild(build: Build): List<SlotPipelineStub> {
        return namedParameterJdbcTemplate!!.query(
            """
                 SELECT E.ID AS ENV_ID, E.NAME AS ENV_NAME,
                        S.ID AS SLOT_ID, S.QUALIFIER AS SLOT_QUALIFIER,
                        P.ID AS PIPELINE_ID, P.STATUS AS PIPELINE_STATUS
                 FROM BUILDS BD
                 INNER JOIN BRANCHES B ON B.ID = BD.BRANCHID
                 INNER JOIN ENV_SLOTS S ON S.PROJECT_ID = B.PROJECTID
                 INNER JOIN ENVIRONMENTS E ON E.ID = S.ENVIRONMENT_ID
                 INNER JOIN ENV_SLOT_PIPELINE P ON P.SLOT_ID = S.ID AND P.BUILD_ID = BD.ID
                 WHERE B.ID = :buildId
                 AND P.STATUS = 'DEPLOYED' 
            """,
            mapOf("buildId" to build.id())
        ) { rs, _ ->
            SlotPipelineStub(
                environmentId = rs.getString("ENV_ID"),
                environmentName = rs.getString("ENV_NAME"),
                slotId = rs.getString("SLOT_ID"),
                qualifier = rs.getString("SLOT_QUALIFIER"),
                pipelineId = rs.getString("PIPELINE_ID"),
            )
        }
    }

}