package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.databind.JsonNode
import com.google.common.base.Function

import java.util.Optional

/**
 * This service allows to store and retrieve arbitrary data with some
 * [project entities][net.nemerosa.ontrack.model.structure.ProjectEntity].
 */
interface EntityDataService {

    /**
     * Stores boolean data
     */
    fun store(entity: ProjectEntity, key: String, value: Boolean)

    /**
     * Stores integer data
     */
    fun store(entity: ProjectEntity, key: String, value: Int)

    /**
     * Stores string data
     */
    fun store(entity: ProjectEntity, key: String, value: String)

    /**
     * Stores arbitrary data as JSON
     */
    fun store(entity: ProjectEntity, key: String, value: Any)

    /**
     * Retrieves data as boolean
     */
    fun retrieveBoolean(entity: ProjectEntity, key: String): Boolean?

    /**
     * Retrieves data as integer
     */
    fun retrieveInteger(entity: ProjectEntity, key: String): Int?

    /**
     * Retrieves arbitrary data as string
     */
    fun retrieve(entity: ProjectEntity, key: String): String?

    /**
     * Retrieves arbitrary data as JSON
     */
    fun retrieveJson(entity: ProjectEntity, key: String): JsonNode?

    /**
     * Retrieves arbitrary data as JSON
     */
    fun <T> retrieve(entity: ProjectEntity, key: String, type: Class<T>): T?

    /**
     * Deletes data
     *
     * @param entity Entity to delete data from
     * @param key    Key to delete
     */
    fun delete(entity: ProjectEntity, key: String)

    /**
     * Loads some data, processes it and saves it back
     */
    fun <T> withData(entity: ProjectEntity, key: String, type: Class<T>, processFn: (T) -> T)

    /**
     * Flexible query based on JSON, to get a first match based on numeric comparison
     */
    fun findFirstJsonFieldGreaterOrEqual(type: ProjectEntityType, reference: Pair<String, Int>, value: Long, vararg jsonPath: String): Int?

}
