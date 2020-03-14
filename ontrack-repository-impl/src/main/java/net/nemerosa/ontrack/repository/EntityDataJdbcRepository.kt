package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class EntityDataJdbcRepository(
        dataSource: DataSource
) : AbstractJdbcRepository(dataSource), EntityDataRepository {

    override fun store(entity: ProjectEntity, key: String, value: String) {
        storeJson(entity, key, TextNode(value))
    }

    override fun storeJson(entity: ProjectEntity, key: String, value: JsonNode) {
        // Existing?
        val existingId = getOptional(
                String.format(
                        "SELECT ID FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.projectEntityType.name
                ),
                params("entityId", entity.id()).addValue("name", key),
                Int::class.java
        )
        if (existingId.isPresent) {
            namedParameterJdbcTemplate!!.update(
                    "UPDATE ENTITY_DATA SET JSON_VALUE = CAST(:value AS JSONB) WHERE ID = :id",
                    params("id", existingId.get()).addValue("value", writeJson(value))
            )
        } else {
            namedParameterJdbcTemplate!!.update(
                    String.format(
                            "INSERT INTO ENTITY_DATA(%s, NAME, JSON_VALUE) VALUES (:entityId, :name, CAST(:value AS JSONB))",
                            entity.projectEntityType.name
                    ),
                    params("entityId", entity.id())
                            .addValue("name", key)
                            .addValue("value", writeJson(value))
            )
        }
    }

    override fun retrieve(entity: ProjectEntity, key: String): String? {
        return retrieveJson(entity, key)?.asText()
    }

    override fun retrieveJson(entity: ProjectEntity, key: String): JsonNode? {
        return getOptional(
                String.format(
                        "SELECT JSON_VALUE FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.projectEntityType.name
                ),
                params("entityId", entity.id()).addValue("name", key),
                String::class.java
        ).map { this.readJson(it) }.orElse(null)
    }

    override fun findEntityByValue(type: ProjectEntityType, key: String, value: JsonNode): ProjectEntityID? {
        return namedParameterJdbcTemplate!!.query(
                """
                    SELECT ${type.name}
                    FROM ENTITY_DATA
                    WHERE ${type.name} IS NOT NULL
                    AND NAME = :key
                    AND JSON_VALUE = :value::jsonb
                    ORDER BY ID DESC
                    LIMIT 1
                """,
                params("key", key).addValue("value", value.format())
        ) { rs: ResultSet, _ ->
            val id = rs.getInt(type.name)
            ProjectEntityID(type, id)
        }.firstOrNull()
    }

    override fun delete(entity: ProjectEntity, key: String) {
        namedParameterJdbcTemplate!!.update(
                String.format(
                        "DELETE FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.projectEntityType.name
                ),
                params("entityId", entity.id()).addValue("name", key)
        )
    }

}
