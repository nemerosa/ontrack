package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;

import java.util.Optional;

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
     * Stores string data
     */
    void store(ProjectEntity entity, String key, String value);

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
    Optional<Integer> retrieveInteger(ProjectEntity entity, String key);

    /**
     * Retrieves arbitrary data as string
     */
    Optional<String> retrieve(ProjectEntity entity, String key);

    /**
     * Retrieves arbitrary data as JSON
     */
    Optional<JsonNode> retrieveJson(ProjectEntity entity, String key);

    /**
     * Retrieves arbitrary data as JSON
     */
    <T> Optional<T> retrieve(ProjectEntity entity, String key, Class<T> type);

    /**
     * Deletes data
     *
     * @param entity Entity to delete data from
     * @param key    Key to delete
     */
    void delete(ProjectEntity entity, String key);

    /**
     * Loads some data, processes it and saves it back
     */
    <T> void withData(ProjectEntity entity, String key, Class<T> type, Function<T, T> processFn);

}
