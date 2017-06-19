package net.nemerosa.ontrack.repository.support.store;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Signature;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EntityDataStore {

    /**
     * Adds a new entry in the store
     *
     * @param entity    Associated entity
     * @param category  Data category
     * @param name      Data name
     * @param signature Data signature
     * @param groupName Data group (optional)
     * @param data      Data
     * @return Data record
     */
    EntityDataStoreRecord add(ProjectEntity entity, String category, String name, Signature signature, String groupName, JsonNode data);

    /**
     * Overrides an entry in the store
     *
     * @param entity    Associated entity
     * @param category  Data category
     * @param name      Data name
     * @param signature Data signature
     * @param groupName Data group (optional)
     * @param data      Data
     * @return Data record
     */
    EntityDataStoreRecord replaceOrAdd(ProjectEntity entity, String category, String name, Signature signature, String groupName, JsonNode data);

    /**
     * Gets the audit data for an entry
     *
     * @param id Record ID
     * @return List of audit records, from the most recent to the oldest
     */
    List<EntityDataStoreRecordAudit> getRecordAudit(int id);

    /**
     * Deletes all entries for a name
     *
     * @param entity   Associated entity
     * @param category Data category
     * @param name     Data name
     */
    void deleteByName(ProjectEntity entity, String category, String name);

    /**
     * Deletes all entries for a group
     *
     * @param entity    Associated entity
     * @param category  Data category
     * @param groupName Data group name
     */
    void deleteByGroup(ProjectEntity entity, String category, String groupName);

    /**
     * Deletes all entries for a category before a given time
     *
     * @param category   Data category
     * @param beforeTime To delete before this time
     */
    void deleteByCategoryBefore(String category, LocalDateTime beforeTime);

    /**
     * Retrieves if any, the last data, for an entity, a category and a name
     *
     * @param entity     Entity associated with the data
     * @param category   Data category
     * @param name       Data key in the category
     * @param beforeTime Last record BEFORE this time
     * @return A reference to the JSON data together with the associated time and user.
     */
    Optional<EntityDataStoreRecord> findLastByCategoryAndName(ProjectEntity entity, String category, String name, LocalDateTime beforeTime);

    /**
     * Retrieves if any, the last data, for an entity, a category, a group and a name
     *
     * @param entity    Entity associated with the data
     * @param category  Data category
     * @param groupName Optional name to group some entries together
     * @param name      Data key in the category
     * @return A reference to the JSON data together with the associated time and user.
     */
    Optional<EntityDataStoreRecord> findLastByCategoryAndGroupAndName(ProjectEntity entity, String category, String groupName, String name);

    /**
     * Gets a list of entries for a given category, getting only the last of each name
     *
     * @param entity   Entity associated with the data
     * @param category Data category
     * @return List of entries
     */
    List<EntityDataStoreRecord> findLastRecordsByNameInCategory(ProjectEntity entity, String category);

    /**
     * Adds a new entry in the store
     *
     * @param entity    Associated entity
     * @param category  Data category
     * @param name      Data name
     * @param signature Data signature
     * @param groupName Data group (optional)
     * @param data      Data
     * @return Data record
     */
    EntityDataStoreRecord addObject(ProjectEntity entity, String category, String name, Signature signature, String groupName, Object data);

    /**
     * Overrides an entry in the store
     *
     * @param entity    Associated entity
     * @param category  Data category
     * @param name      Data name
     * @param signature Data signature
     * @param groupName Data group (optional)
     * @param data      Data
     * @return Data record
     */
    EntityDataStoreRecord replaceOrAddObject(ProjectEntity entity, String category, String name, Signature signature, String groupName, Object data);

    /**
     * Gets a record by ID
     *
     * @param id ID of the record
     * @return Record or empty if not found
     */
    Optional<EntityDataStoreRecord> getById(ProjectEntity entity, int id);
}
