package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertySearchArguments;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static java.lang.String.format;

@Repository
public class PropertyJdbcRepository extends AbstractJdbcRepository implements PropertyRepository {

    @Autowired
    public PropertyJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    static void prepareQueryForPropertyValue(
            PropertySearchArguments searchArguments,
            StringBuilder tables,
            StringBuilder criteria,
            MapSqlParameterSource params
    ) {
        if (StringUtils.isNotBlank(searchArguments.getJsonContext())) {
            tables.append(format(" LEFT JOIN %s on true", searchArguments.getJsonContext()));
        }
        if (StringUtils.isNotBlank(searchArguments.getJsonCriteria())) {
            criteria.append(format(" AND %s", searchArguments.getJsonCriteria()));
            if (searchArguments.getCriteriaParams() != null) {
                for (Map.Entry<String, ?> entry : searchArguments.getCriteriaParams().entrySet()) {
                    params.addValue(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Override
    @Cacheable(cacheNames = "properties", key = "#typeName + #entityType.name() + #entityId.value")
    public TProperty loadProperty(String typeName, ProjectEntityType entityType, ID entityId) {
        return getFirstItem(
                String.format(
                        "SELECT * FROM PROPERTIES WHERE TYPE = :type AND %s = :entityId",
                        entityType.name()
                ),
                params("type", typeName).addValue("entityId", entityId.getValue()),
                (rs, rowNum) -> toProperty(rs)
        );
    }

    @Override
    @CacheEvict(cacheNames = "properties", key = "#typeName + #entityType.name() + #entityId.value")
    public void saveProperty(String typeName, ProjectEntityType entityType, ID entityId, JsonNode data) {
        MapSqlParameterSource params = params("type", typeName).addValue("entityId", entityId.getValue());
        // Any previous value?
        Integer propertyId = getFirstItem(
                String.format(
                        "SELECT ID FROM PROPERTIES WHERE TYPE = :type AND %s = :entityId",
                        entityType.name()
                ),
                params,
                Integer.class
        );
        // Data parameters
        params.addValue("json", writeJson(data));
        // Update
        if (propertyId != null) {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE PROPERTIES SET JSON = CAST(:json AS JSONB) WHERE ID = :id",
                    params.addValue("id", propertyId)
            );
        }
        // Creation
        else {
            getNamedParameterJdbcTemplate().update(
                    String.format(
                            "INSERT INTO PROPERTIES(TYPE, %s, JSON) " +
                                    "VALUES(:type, :entityId, CAST(:json AS JSONB))",
                            entityType.name()
                    ),
                    params
            );
        }
    }

    @Override
    @CacheEvict(cacheNames = "properties", key = "#typeName + #entityType.name() + #entityId.value")
    public Ack deleteProperty(String typeName, ProjectEntityType entityType, ID entityId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        String.format(
                                "DELETE FROM PROPERTIES WHERE TYPE = :type AND %s = :entityId",
                                entityType.name()
                        ),
                        params("type", typeName).addValue("entityId", entityId.getValue())
                )
        );
    }

    @Override
    public Collection<ProjectEntity> searchByProperty(String typeName,
                                                      BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                                                      Predicate<TProperty> predicate) {
        return getNamedParameterJdbcTemplate().execute(
                "SELECT * FROM PROPERTIES WHERE TYPE = :type ORDER BY ID DESC",
                params("type", typeName),
                (PreparedStatement ps) -> {
                    Collection<ProjectEntity> entities = new ArrayList<>();
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        TProperty t = toProperty(rs);
                        if (predicate.test(t)) {
                            entities.add(entityLoader.apply(t.getEntityType(), t.getEntityId()));
                        }
                    }
                    return entities;
                }
        );
    }

    @Override
    @Nullable
    public ID findBuildByBranchAndSearchkey(ID branchId, String typeName, PropertySearchArguments searchArguments) {
        StringBuilder tables = new StringBuilder(
                "SELECT b.ID " +
                        "FROM PROPERTIES p " +
                        "INNER JOIN BUILDS b ON p.BUILD = b.ID "
        );
        StringBuilder criteria = new StringBuilder(
                "WHERE p.TYPE = :type " +
                        "AND b.BRANCHID = :branchId"
        );
        MapSqlParameterSource params = params("type", typeName)
                .addValue("branchId", branchId.getValue());
        if (searchArguments != null) {
            prepareQueryForPropertyValue(
                    searchArguments,
                    tables,
                    criteria,
                    params
            );
        }
        String sql = tables + " " + criteria;
        Integer id = getFirstItem(
                sql,
                params,
                Integer.class
        );
        if (id != null) {
            return ID.of(id);
        } else {
            return null;
        }
    }

    private TProperty toProperty(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String typeName = rs.getString("type");
        // Detects the entity
        ProjectEntityType entityType = null;
        ID entityId = null;
        for (ProjectEntityType candidate : ProjectEntityType.values()) {
            int candidateId = rs.getInt(candidate.name());
            if (!rs.wasNull()) {
                entityType = candidate;
                entityId = ID.of(candidateId);
            }
        }
        // Sanity check
        if (entityType == null || !ID.isDefined(entityId)) {
            throw new IllegalStateException(
                    String.format(
                            "Could not find any entity for property %s with id = %d",
                            typeName,
                            id
                    )
            );
        }
        // OK
        return new TProperty(
                typeName,
                entityType,
                entityId,
                readJson(rs, "json")
        );
    }
}
