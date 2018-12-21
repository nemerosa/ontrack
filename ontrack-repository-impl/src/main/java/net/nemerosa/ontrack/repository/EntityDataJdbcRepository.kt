package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.util.*
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
            namedParameterJdbcTemplate.update(
                    "UPDATE ENTITY_DATA SET JSON_VALUE = CAST(:value AS JSONB) WHERE ID = :id",
                    params("id", existingId.get()).addValue("value", writeJson(value))
            )
        } else {
            namedParameterJdbcTemplate.update(
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

    override fun retrieve(entity: ProjectEntity, key: String): Optional<String> {
        return retrieveJson(entity, key).map { it.asText() }
    }

    override fun retrieveJson(entity: ProjectEntity, key: String): Optional<JsonNode> {
        return getOptional(
                String.format(
                        "SELECT JSON_VALUE FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.projectEntityType.name
                ),
                params("entityId", entity.id()).addValue("name", key),
                String::class.java
        ).map { this.readJson(it) }
    }

    override fun delete(entity: ProjectEntity, key: String) {
        namedParameterJdbcTemplate.update(
                String.format(
                        "DELETE FROM ENTITY_DATA WHERE %s = :entityId AND NAME = :name",
                        entity.projectEntityType.name
                ),
                params("entityId", entity.id()).addValue("name", key)
        )
    }

    override fun findFirstJsonFieldGreaterOrEqual(type: ProjectEntityType, reference: Pair<String, Int>, value: Long, vararg jsonPath: String): Int? {
        val length = jsonPath.size
        val jsonCriteria = jsonPath.mapIndexed { index, it ->
            if (index == length - 1) {
                "->>$it"
            } else {
                "->$it"
            }
        }.joinToString("")
        val sql = """
            SELECT e.${type.name}
            FROM ENTITY_DATA e
            INNER JOIN ${type.name}S x ON x.ID = e.${type.name}
            WHERE x.${reference.first} = :referenceId
            AND CAST(e.json_value$jsonCriteria AS numeric) >= :value
        """
        return getFirstItem(sql, params("referenceId", reference.second).addValue("value", value), Int::class.java)
    }
}
