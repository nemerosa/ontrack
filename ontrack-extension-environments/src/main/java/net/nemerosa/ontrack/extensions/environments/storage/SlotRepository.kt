package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
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

}