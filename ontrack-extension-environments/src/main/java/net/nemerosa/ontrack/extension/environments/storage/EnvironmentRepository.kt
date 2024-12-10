package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.EnvironmentFilter
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import net.nemerosa.ontrack.repository.support.getDocumentWithType
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class EnvironmentRepository(
    dataSource: DataSource,
) : AbstractJdbcRepository(dataSource), EnvironmentRepositoryAccessor {

    val countEnvironments: Int
        get() =
            jdbcTemplate!!.queryForObject(
                """
                SELECT COUNT(*)
                FROM ENVIRONMENTS
            """.trimIndent(),
                Int::class.java
            ) ?: 0

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
                SELECT ID, NAME, "ORDER", DESCRIPTION, TAGS, (IMAGE IS NOT NULL) AS HAS_IMAGE
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
                SELECT ID, NAME, "ORDER", DESCRIPTION, TAGS, (IMAGE IS NOT NULL) AS HAS_IMAGE 
                FROM ENVIRONMENTS
                WHERE NAME = :name
            """,
            mapOf("name" to name)
        ) { rs, _ ->
            toEnvironment(rs)
        }.firstOrNull()

    fun findAll(filter: EnvironmentFilter): List<Environment> {
        val joins = mutableListOf<String>()
        val criteria = mutableListOf<String>()
        val params = mutableMapOf<String, Any?>()

        if (!filter.tags.isNullOrEmpty()) {
            criteria += "E.TAGS @> :tags::text[]"
            params["tags"] = filter.tags.toTypedArray()
        }

        if (!filter.projects.isNullOrEmpty()) {
            joins += """
                INNER JOIN ENV_SLOTS S ON S.ENVIRONMENT_ID = E.ID
                INNER JOIN PROJECTS P ON P.ID = S.PROJECT_ID
            """.trimIndent()
            criteria += "P.NAME = ANY(:projects)"
            params["projects"] = filter.projects.toTypedArray()
        }

        var sql = """
            SELECT E.ID, E.NAME, E."ORDER", E.DESCRIPTION, E.TAGS, (E.IMAGE IS NOT NULL) AS HAS_IMAGE
            FROM ENVIRONMENTS E
            
        """.trimIndent()
        if (joins.isNotEmpty()) {
            sql += joins.joinToString(" ")
        }
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
        tags = (rs.getArray("TAGS").array as Array<String>).toList(),
        image = rs.getBoolean("HAS_IMAGE"),
    )

    fun setEnvironmentImage(environment: Environment, document: Document?) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE ENVIRONMENTS
                SET IMAGE = :image
                WHERE ID = :id
            """.trimIndent(),
            mapOf(
                "id" to environment.id,
                "image" to document?.content
            )
        )
    }

    fun getEnvironmentImage(environment: Environment): Document =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT IMAGE
                FROM ENVIRONMENTS
                WHERE ID = :id
            """.trimIndent(),
            mapOf(
                "id" to environment.id,
            )
        ) { rs, _ -> rs.getDocumentWithType("IMAGE", "image/png") }
            .firstOrNull()
            ?: Document.EMPTY

}