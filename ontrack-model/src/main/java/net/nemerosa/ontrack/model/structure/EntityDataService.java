package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * This service allows to store and retrieve arbitrary data with some
 * {@linkplain net.nemerosa.ontrack.model.structure.ProjectEntity project entities}.
 */
public interface EntityDataService {

    /**
     * Stores boolean data
     */
    void store(ProjectEntity entity, String key, boolean value);

    /**
     * Stores integer data
     */
    void store(ProjectEntity entity, String key, int value);

    /**
     * Stores arbitrary data as JSON
     */
    void store(ProjectEntity entity, String key, Object value);

    /**
     * Retrieves data as boolean
     */
    Optional<Boolean> retrieveBoolean(ProjectEntity entity, String key);

    /**
     * Retrieves data as integer
     */
    OptionalInt retrieveInteger(ProjectEntity entity, String key);

    /**
     * Retrieves arbitrary data as JSON
     */
    JsonNode retrieveJson(ProjectEntity entity, String key);

    /**
     * Retrieves arbitrary data as JSON
     */
    <T> T retrieve(ProjectEntity entity, String key, Class<T> type);

    /**
     * Deletes data
     *
     * @param entity Entity to delete data from
     * @param key    Key to delete
     */
    void delete(ProjectEntity entity, String key);

}
