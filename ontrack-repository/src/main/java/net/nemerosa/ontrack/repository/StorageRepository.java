package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores and retrieves arbitrary data using JSON.
 */
public interface StorageRepository {

    /**
     * Stores some JSON
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @param node  Data to store (null to delete)
     */
    void storeJson(String store, String key, JsonNode node);

    /**
     * Retrieves some JSON using a key
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @return Data or empty if not found
     */
    Optional<JsonNode> retrieveJson(String store, String key);

    /**
     * Lists all keys for a store
     *
     * @param store Store (typically an extension class name)
     */
    List<String> getKeys(String store);

    /**
     * Gets all the data for a store
     *
     * @param store Store (typically an extension class name)
     */
    Map<String, JsonNode> getData(String store);

    /**
     * Deletes an entry
     */
    void delete(String store, String key);

    /**
     * Checks if an entry already exists.
     *
     * @param store Store to check
     * @param key Key to check
     * @return <code>true</code> if the entry exists
     */
    boolean exists(String store, String key);
}
