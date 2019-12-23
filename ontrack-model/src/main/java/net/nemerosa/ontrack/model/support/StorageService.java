package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stores and retrieves arbitrary data using JSON.
 */
public interface StorageService {

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
     * Stores some object after having formatted it in JSON
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @param data  Data to store (null to delete)
     */
    default void store(String store, String key, Object data) {
        storeJson(store, key, JsonUtils.format(data));
    }

    /**
     * Retrieves some data using a key
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @param type  Class of the data to retrieve
     * @return Data or empty if not found
     */
    default <T> Optional<T> retrieve(String store, String key, Class<T> type) {
        return retrieveJson(store, key)
                .map(node -> JsonUtils.parse(node, type));
    }

    /**
     * Deletes an entry
     */
    void delete(String store, String key);


    /**
     * Gets all the data for a store
     *
     * @param store Store (typically an extension class name)
     * @param type  Class of the data to retrieve
     */
    default <T> Map<String, T> getData(String store, Class<T> type) {
        return getData(store).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> JsonUtils.parse(entry.getValue(), type)
                ));
    }
}
