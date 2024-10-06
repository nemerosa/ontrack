package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class EnvironmentRepository(
    dataSource: DataSource,
) : AbstractJdbcRepository(dataSource), EnvironmentRepositoryAccessor {

    fun save(environment: Environment) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO ENVIRONMENTS (ID, NAME, "ORDER", DESCRIPTION)
                VALUES (:id, :name, :order, :description)
                ON CONFLICT (ID) DO UPDATE SET
                    NAME = EXCLUDED.NAME,
                    "ORDER" = EXCLUDED."ORDER",
                    DESCRIPTION = EXCLUDED.DESCRIPTION;
            """,
            mapOf(
                "id" to environment.id,
                "name" to environment.name,
                "order" to environment.order,
                "description" to environment.description,
            )
        )
    }

    override fun getEnvironmentById(id: String): Environment {
        return try {
            namedParameterJdbcTemplate!!.queryForObject(
                """
                SELECT *
                FROM ENVIRONMENTS
                WHERE ID = :id
            """,
                mapOf("id" to id),
            ) { rs, _ ->
                toEnvironment(rs)
            } ?: throw EnvironmentIdNotFoundException(id)
        } catch (ex: IncorrectResultSizeDataAccessException) {
            throw EnvironmentIdNotFoundException(id)
        }
    }

    fun findByName(name: String): Environment? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENVIRONMENTS
                WHERE NAME = :name
            """,
            mapOf("name" to name)
        ) { rs, _ ->
            toEnvironment(rs)
        }.firstOrNull()

    fun findAll(): List<Environment> =
        namedParameterJdbcTemplate!!.query(
            """
                    SELECT *
                    FROM ENVIRONMENTS
                    ORDER BY "ORDER"
            """,
        ) { rs, _ ->
            toEnvironment(rs)
        }

    fun delete(env: Environment) {
        namedParameterJdbcTemplate!!.update(
            """
               DELETE FROM ENVIRONMENTS
                WHERE ID = :id
            """,
            mapOf("id" to env.id)
        )
    }

    private fun toEnvironment(rs: ResultSet) = Environment(
        id = rs.getString("ID"),
        name = rs.getString("NAME"),
        order = rs.getInt("ORDER"),
        description = rs.getString("DESCRIPTION"),
    )

}