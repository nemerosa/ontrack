package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.EnvironmentFilter
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
                INSERT INTO ENVIRONMENTS (ID, NAME, "ORDER", DESCRIPTION, TAGS)
                VALUES (:id, :name, :order, :description, :tags)
                ON CONFLICT (ID) DO UPDATE SET
                    NAME = EXCLUDED.NAME,
                    "ORDER" = EXCLUDED."ORDER",
                    DESCRIPTION = EXCLUDED.DESCRIPTION,
                    TAGS = EXCLUDED.TAGS;
            """,
            mapOf(
                "id" to environment.id,
                "name" to environment.name,
                "order" to environment.order,
                "description" to environment.description,
                "tags" to environment.tags.toTypedArray(),
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

    fun findAll(filter: EnvironmentFilter): List<Environment> {
        val criteria = mutableListOf<String>()
        val params = mutableMapOf<String, Any?>()

        if (filter.tags.isNotEmpty()) {
            criteria += "TAGS @> :tags::text[]"
            params["tags"] = filter.tags.toTypedArray()
        }

        var sql = "SELECT * FROM ENVIRONMENTS "
        if (criteria.isNotEmpty()) {
            sql += " WHERE "
            sql += criteria.joinToString(" AND ") { "( $it )" }
        }
        sql += """ ORDER BY "ORDER" """

        return namedParameterJdbcTemplate!!.query(
            sql,
            params,
        ) { rs, _ ->
            toEnvironment(rs)
        }
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

    @Suppress("UNCHECKED_CAST")
    private fun toEnvironment(rs: ResultSet) = Environment(
        id = rs.getString("ID"),
        name = rs.getString("NAME"),
        order = rs.getInt("ORDER"),
        description = rs.getString("DESCRIPTION"),
        tags = (rs.getArray("TAGS").array as Array<String>).toList()
    )

}