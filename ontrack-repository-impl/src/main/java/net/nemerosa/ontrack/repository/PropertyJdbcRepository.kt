package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import java.lang.String.format
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import java.util.function.BiFunction
import java.util.function.Predicate
import javax.sql.DataSource

@Repository
class PropertyJdbcRepository @Autowired
constructor(dataSource: DataSource) : AbstractJdbcRepository(dataSource), PropertyRepository {

    @Cacheable(cacheNames = ["properties"], key = "#typeName + #entityType.name() + #entityId.value")
    override fun loadProperty(typeName: String, entityType: ProjectEntityType, entityId: ID): TProperty {
        return getFirstItem(
                String.format(
                        "SELECT * FROM PROPERTIES WHERE TYPE = :type AND %s = :entityId",
                        entityType.name
                ),
                params("type", typeName).addValue("entityId", entityId.value)
        ) { rs, rowNum -> toProperty(rs) }
    }

    @CacheEvict(cacheNames = ["properties"], key = "#typeName + #entityType.name() + #entityId.value")
    override fun saveProperty(typeName: String, entityType: ProjectEntityType, entityId: ID, data: JsonNode) {
        val params = params("type", typeName).addValue("entityId", entityId.value)
        // Any previous value?
        val propertyId = getFirstItem(
                String.format(
                        "SELECT ID FROM PROPERTIES WHERE TYPE = :type AND %s = :entityId",
                        entityType.name
                ),
                params,
                Int::class.java
        )
        // Data parameters
        params.addValue("json", writeJson(data))
        // Update
        if (propertyId != null) {
            namedParameterJdbcTemplate.update(
                    "UPDATE PROPERTIES SET JSON = CAST(:json AS JSONB) WHERE ID = :id",
                    params.addValue("id", propertyId)
            )
        } else {
            namedParameterJdbcTemplate.update(
                    String.format(
                            "INSERT INTO PROPERTIES(TYPE, %s, JSON) " + "VALUES(:type, :entityId, CAST(:json AS JSONB))",
                            entityType.name
                    ),
                    params
            )
        }// Creation
    }

    @CacheEvict(cacheNames = ["properties"], key = "#typeName + #entityType.name() + #entityId.value")
    override fun deleteProperty(typeName: String, entityType: ProjectEntityType, entityId: ID): Ack {
        return Ack.one(
                namedParameterJdbcTemplate.update(
                        String.format(
                                "DELETE FROM PROPERTIES WHERE TYPE = :type AND %s = :entityId",
                                entityType.name
                        ),
                        params("type", typeName).addValue("entityId", entityId.value)
                )
        )
    }

    override fun searchByProperty(typeName: String,
                                  entityLoader: BiFunction<ProjectEntityType, ID, ProjectEntity>,
                                  predicate: Predicate<TProperty>): Collection<ProjectEntity> {
        return namedParameterJdbcTemplate.execute<Collection<ProjectEntity>>(
                "SELECT * FROM PROPERTIES WHERE TYPE = :type ORDER BY ID DESC",
                params("type", typeName)
        ) { ps: PreparedStatement ->
            val entities = ArrayList<ProjectEntity>()
            val rs = ps.executeQuery()
            while (rs.next()) {
                val t = toProperty(rs)
                if (predicate.test(t)) {
                    entities.add(entityLoader.apply(t.entityType, t.entityId))
                }
            }
            entities
        }
    }

    override fun findBuildByBranchAndSearchkey(branchId: ID, typeName: String, searchArguments: PropertySearchArguments): ID? {
        val tables = StringBuilder(
                "SELECT b.ID " +
                        "FROM PROPERTIES p " +
                        "INNER JOIN BUILDS b ON p.BUILD = b.ID "
        )
        val criteria = StringBuilder(
                "WHERE p.TYPE = :type " + "AND b.BRANCHID = :branchId"
        )
        val params = params("type", typeName)
                .addValue("branchId", branchId.value)
        if (searchArguments != null) {
            prepareQueryForPropertyValue(
                    searchArguments,
                    tables,
                    criteria,
                    params
            )
        }
        val sql = "$tables $criteria"
        val id = getFirstItem(
                sql,
                params,
                Int::class.java
        )
        return if (id != null) {
            ID.of(id)
        } else {
            null
        }
    }

    @Throws(SQLException::class)
    private fun toProperty(rs: ResultSet): TProperty {
        val id = rs.getInt("id")
        val typeName = rs.getString("type")
        // Detects the entity
        var entityType: ProjectEntityType? = null
        var entityId: ID? = null
        for (candidate in ProjectEntityType.values()) {
            val candidateId = rs.getInt(candidate.name)
            if (!rs.wasNull()) {
                entityType = candidate
                entityId = ID.of(candidateId)
            }
        }
        // Sanity check
        if (entityType == null || !ID.isDefined(entityId)) {
            throw IllegalStateException(
                    String.format(
                            "Could not find any entity for property %s with id = %d",
                            typeName,
                            id
                    )
            )
        }
        // OK
        return TProperty(
                typeName,
                entityType,
                entityId!!,
                readJson(rs, "json")
        )
    }

    companion object {
        @JvmStatic
        fun prepareQueryForPropertyValue(
                searchArguments: PropertySearchArguments,
                tables: StringBuilder,
                criteria: StringBuilder,
                params: MapSqlParameterSource
        ) {
            if (StringUtils.isNotBlank(searchArguments.jsonContext)) {
                tables.append(format(" LEFT JOIN %s on true", searchArguments.jsonContext))
            }
            if (StringUtils.isNotBlank(searchArguments.jsonCriteria)) {
                criteria.append(format(" AND %s", searchArguments.jsonCriteria))
                if (searchArguments.criteriaParams != null) {
                    for ((key, value) in searchArguments.criteriaParams!!) {
                        params.addValue(key, value)
                    }
                }
            }
        }
    }
}
