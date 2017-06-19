package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import net.nemerosa.ontrack.repository.support.store.EntityDataStore;
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecord;
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecordAudit;
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecordAuditType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EntityDataStoreJdbcRepository extends AbstractJdbcRepository implements EntityDataStore {

    @Autowired
    public EntityDataStoreJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public EntityDataStoreRecord add(ProjectEntity entity, String category, String name, Signature signature, String groupName, JsonNode data) {
        int id = dbCreate(
                String.format(
                        "INSERT INTO ENTITY_DATA_STORE(%s, CATEGORY, NAME, GROUPID, JSON, CREATION, CREATOR) VALUES (:entityId, :category, :name, :groupId, :json, :creation, :creator)",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id())
                        .addValue("category", category)
                        .addValue("name", name)
                        .addValue("groupId", groupName)
                        .addValue("json", writeJson(data))
                        .addValue("creation", dateTimeForDB(signature.getTime()))
                        .addValue("creator", signature.getUser().getName())
        );
        // Audit
        audit(EntityDataStoreRecordAuditType.CREATED, id, signature);
        // OK
        return new EntityDataStoreRecord(
                id,
                entity,
                category,
                name,
                groupName,
                signature,
                data
        );
    }

    @Override
    public EntityDataStoreRecord replaceOrAdd(ProjectEntity entity, String category, String name, Signature signature, String groupName, JsonNode data) {
        // Gets the last ID by category and name
        Integer id = getFirstItem(
                String.format(
                        "SELECT ID FROM ENTITY_DATA_STORE " +
                                "WHERE %s = :entityId " +
                                "AND CATEGORY = :category " +
                                "AND NAME = :name " +
                                "ORDER BY ID DESC " +
                                "LIMIT 1",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id())
                        .addValue("category", category)
                        .addValue("name", name),
                Integer.class
        );
        // Existing record
        if (id != null) {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE ENTITY_DATA_STORE SET " +
                            "CREATION = :creation, " +
                            "CREATOR = :creator, " +
                            "JSON = :json, " +
                            "GROUPID = :groupId " +
                            "WHERE ID = :id",
                    params("id", id)
                            .addValue("groupId", groupName)
                            .addValue("json", writeJson(data))
                            .addValue("creation", dateTimeForDB(signature.getTime()))
                            .addValue("creator", signature.getUser().getName())
            );
            audit(EntityDataStoreRecordAuditType.UPDATED, id, signature);
            return new EntityDataStoreRecord(
                    id,
                    entity,
                    category,
                    name,
                    groupName,
                    signature,
                    data
            );
        }
        // New record
        else {
            return add(entity, category, name, signature, groupName, data);
        }
    }

    @Override
    public List<EntityDataStoreRecordAudit> getRecordAudit(int id) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM ENTITY_DATA_STORE_AUDIT " +
                        "WHERE RECORD_ID = :recordId " +
                        "ORDER BY ID DESC",
                params("recordId", id),
                (rs, rowNum) -> new EntityDataStoreRecordAudit(
                        EntityDataStoreRecordAuditType.valueOf(rs.getString("AUDIT_TYPE")),
                        readSignature(rs, "TIMESTAMP", "USER")
                )
        );
    }

    @Override
    public void deleteByName(ProjectEntity entity, String category, String name) {
        getNamedParameterJdbcTemplate().update(
                String.format(
                        "DELETE FROM ENTITY_DATA_STORE " +
                                "WHERE %s = :entityId " +
                                "AND CATEGORY = :category " +
                                "AND NAME = :name",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id())
                        .addValue("category", category)
                        .addValue("name", name)
        );
    }

    @Override
    public void deleteByGroup(ProjectEntity entity, String category, String groupName) {
        getNamedParameterJdbcTemplate().update(
                String.format(
                        "DELETE FROM ENTITY_DATA_STORE " +
                                "WHERE %s = :entityId " +
                                "AND CATEGORY = :category " +
                                "AND GROUPID = :groupId",
                        entity.getProjectEntityType().name()
                ),
                params("entityId", entity.id())
                        .addValue("category", category)
                        .addValue("groupId", groupName)
        );
    }

    @Override
    public void deleteByCategoryBefore(String category, LocalDateTime beforeTime) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM ENTITY_DATA_STORE " +
                        "WHERE CATEGORY = :category " +
                        "AND CREATION <= :beforeTime",
                params("category", category)
                        .addValue("beforeTime", dateTimeForDB(beforeTime))
        );
    }

    @Override
    public Optional<EntityDataStoreRecord> findLastByCategoryAndName(ProjectEntity entity, String category, String name, LocalDateTime beforeTime) {
        // SQL & parameters
        String sql = String.format(
                "SELECT * FROM ENTITY_DATA_STORE " +
                        "WHERE %s = :entityId AND CATEGORY = :category AND NAME = :name ",
                entity.getProjectEntityType().name()
        );
        MapSqlParameterSource params = params("entityId", entity.id())
                .addValue("category", category)
                .addValue("name", name);
        // Time criteria
        if (beforeTime != null) {
            sql += "AND CREATION <= :beforeTime ";
            params = params.addValue("beforeTime", dateTimeForDB(beforeTime));
        }
        // Ordering
        sql += "ORDER BY CREATION DESC LIMIT 1";
        // Performs the query
        return getOptional(
                sql,
                params,
                (rs, rowNum) -> toEntityDataStoreRecord(entity, rs)
        );
    }

    @Override
    public Optional<EntityDataStoreRecord> findLastByCategoryAndGroupAndName(ProjectEntity entity, String category, String groupName, String name) {
        return getLastByName(
                getNamedParameterJdbcTemplate().query(
                        String.format(
                                "SELECT * FROM ENTITY_DATA_STORE " +
                                        "WHERE %s = :entityId " +
                                        "AND CATEGORY = :category " +
                                        "AND GROUPID = :groupId " +
                                        "AND NAME = :name",
                                entity.getProjectEntityType().name()
                        ),
                        params("entityId", entity.id())
                                .addValue("category", category)
                                .addValue("groupId", groupName)
                                .addValue("name", name),
                        (rs, rowNum) -> toEntityDataStoreRecord(entity, rs)
                )
        ).stream().findFirst();
    }

    @Override
    public List<EntityDataStoreRecord> findLastByCategory(ProjectEntity entity, String category) {
        return getLastByName(
                getNamedParameterJdbcTemplate().query(
                        String.format(
                                "SELECT * FROM ENTITY_DATA_STORE " +
                                        "WHERE %s = :entityId " +
                                        "AND CATEGORY = :category",
                                entity.getProjectEntityType().name()
                        ),
                        params("entityId", entity.id())
                                .addValue("category", category),
                        (rs, rowNum) -> toEntityDataStoreRecord(entity, rs)
                )
        );
    }

    private List<EntityDataStoreRecord> getLastByName(List<EntityDataStoreRecord> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(EntityDataStoreRecord::getName))
                // Gets each list separately
                .values().stream()
                // Sorts each list from the newest to the oldest
                .map(list -> list.stream()
                        .sorted(Comparator.comparing((EntityDataStoreRecord e) -> e.getSignature().getTime()).reversed())
                        .findFirst()
                )
                // Gets only the non empty lists
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private EntityDataStoreRecord toEntityDataStoreRecord(ProjectEntity entity, ResultSet rs) throws SQLException {
        return new EntityDataStoreRecord(
                rs.getInt("ID"),
                entity,
                rs.getString("CATEGORY"),
                rs.getString("NAME"),
                rs.getString("GROUPID"),
                readSignature(rs),
                readJson(rs, "JSON")
        );
    }

    @Override
    public EntityDataStoreRecord addObject(ProjectEntity entity, String category, String name, Signature signature, String groupName, Object data) {
        return add(
                entity,
                category,
                name,
                signature,
                groupName,
                JsonUtils.format(data)
        );
    }

    @Override
    public EntityDataStoreRecord replaceOrAddObject(ProjectEntity entity, String category, String name, Signature signature, String groupName, Object data) {
        return replaceOrAdd(
                entity,
                category,
                name,
                signature,
                groupName,
                JsonUtils.format(data)
        );
    }

    private void audit(EntityDataStoreRecordAuditType type, int recordId, Signature signature) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO ENTITY_DATA_STORE_AUDIT(RECORD_ID, AUDIT_TYPE, TIMESTAMP, USER) " +
                        "VALUES (:recordId, :auditType, :timestamp, :user)",
                params("recordId", recordId)
                        .addValue("auditType", type.name())
                        .addValue("timestamp", dateTimeForDB(signature.getTime()))
                        .addValue("user", signature.getUser().getName())
        );
    }
}
