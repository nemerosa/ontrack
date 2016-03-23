package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;

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
}
