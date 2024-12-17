package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ProjectJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), ProjectRepository,
    ProjectJdbcRepositoryAccessor {

    override fun getProject(id: ID): Project =
        getFirstItem(
            """
                SELECT *
                FROM  projects
                WHERE id = :id
            """,
            params("id", id.value)
        ) { rs, _ ->
            Project(
                id = id(rs),
                name = rs.getString("name"),
                description = rs.getString("description"),
                isDisabled = rs.getBoolean("disabled"),
                signature = readSignature(rs),
            )
        }

    override fun lastActiveProjects(): List<Project> {
        return jdbcTemplate!!.query(
            """
                SELECT p.*
                FROM projects p
                         LEFT JOIN branches b ON b.projectid = p.id
                         LEFT JOIN builds bl ON bl.branchid = b.id
                GROUP BY p.id
                ORDER BY MAX(COALESCE(bl.creation, b.creation, p.creation)) DESC; 
            """
        ) { rs, _ ->
            toProject(rs)
        }
    }

}