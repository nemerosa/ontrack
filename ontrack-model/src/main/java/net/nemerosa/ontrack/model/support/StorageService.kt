package net.nemerosa.ontrack.model.support

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseInto
import java.util.*
import kotlin.reflect.KClass

/**
 * Stores and retrieves arbitrary data using JSON.
 */
interface StorageService {
    /**
     * Stores some JSON
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @param node  Data to store (null to delete)
     */
    fun storeJson(store: String, key: String, node: JsonNode?)

    /**
     * Retrieves some JSON using a key
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @return Data or empty if not found
     */
    @Deprecated("Use retrieveJsonOrNull. Will be removed in V5.")
    fun retrieveJson(store: String, key: String): Optional<JsonNode>

    /**
     * Retrieves some JSON using a key
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @return Data or null if not found
     */
    fun findJson(store: String, key: String): JsonNode?

    /**
     * Lists all keys for a store
     *
     * @param store Store (typically an extension class name)
     */
    fun getKeys(store: String): List<String>

    /**
     * Gets all the data for a store
     *
     * @param store Store (typically an extension class name)
     */
    fun getData(store: String): Map<String, JsonNode>

    /**
     * Stores some object after having formatted it in JSON
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @param data  Data to store (null to delete)
     */
    fun store(store: String, key: String, data: Any?) {
        storeJson(store, key, data?.asJson())
    }

    /**
     * Retrieves some data using a key
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @param type  Class of the data to retrieve
     * @return Data or empty if not found
     */
    @Deprecated("Use find instead. Will be removed in V5.")
    fun <T> retrieve(store: String, key: String, type: Class<T>): Optional<T> {
        @Suppress("DEPRECATION")
        return retrieveJson(store, key)
            .map { node: JsonNode -> JsonUtils.parse(node, type) }
    }

    /**
     * Retrieves some data using a key
     *
     * @param store Store (typically an extension class name)
     * @param key   Identifier of data
     * @param type  Class of the data to retrieve
     * @return Data or null if not found
     */
    fun <T : Any> find(store: String, key: String, type: KClass<T>): T? {
        return findJson(store, key)?.parseInto(type)
    }

    /**
     * Deletes an entry
     */
    fun delete(store: String, key: String)

    /**
     * Clears the whole store
     */
    fun clear(store: String)

    /**
     * Checks if an entry already exists.
     *
     * @param store Store to check
     * @param key Key to check
     * @return `true` if the entry exists
     */
    fun exists(store: String, key: String): Boolean

    /**
     * Looking for stored entries using JSON queries
     */
    @Deprecated("Use filter instead. Will be removed in V5.")
    fun <T> findByJson(
        store: String,
        query: String,
        variables: Map<String, *>,
        type: Class<T>,
    ): List<T>

    /**
     * Gets the count of items in a store matching some criteria.
     */
    fun count(
        store: String,
        query: String? = null,
        queryVariables: Map<String, *>? = null,
    ): Int

    /**
     * Gets items in a store matching some criteria.
     */
    fun <T : Any> filter(
        store: String,
        type: KClass<T>,
        offset: Int = 0,
        size: Int = 40,
        context: String = "",
        query: String? = null,
        queryVariables: Map<String, *>? = null,
        orderQuery: String? = null,
    ): List<T>

    /**
     * Gets items in a store matching some criteria.
     */
    fun <T : Any> filterRecords(
        store: String,
        type: KClass<T>,
        offset: Int = 0,
        size: Int = 40,
        context: String = "",
        query: String? = null,
        queryVariables: Map<String, *>? = null,
        orderQuery: String? = null,
    ): Map<String, T>

    /**
     * Deletes items in a store matching some criteria.
     *
     * @return Number of items having been deleted
     */
    fun deleteWithFilter(
        store: String,
        query: String? = null,
        queryVariables: Map<String, *>? = null,
    ): Int

    /**
     * Gets all the data for a store
     *
     * @param store Store (typically an extension class name)
     * @param type  Class of the data to retrieve
     */
    fun <T> getData(store: String, type: Class<T>): Map<String, T> {
        return getData(store)
            .mapValues { (_, value) ->
                JsonUtils.parse(value, type)
            }
    }
}