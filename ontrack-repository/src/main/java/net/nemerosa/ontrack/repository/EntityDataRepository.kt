package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import java.util.*

/**
 * Store for data associated with project entities.
 */
interface EntityDataRepository {

    /**
     * Store
     */
    fun store(entity: ProjectEntity, key: String, value: String)

    /**
     * Retrieve
     */
    fun retrieve(entity: ProjectEntity, key: String): String?

    /**
     * Store JSON
     */
    fun storeJson(entity: ProjectEntity, key: String, value: JsonNode)

    /**
     * Retrieve JSON
     */
    fun retrieveJson(entity: ProjectEntity, key: String): JsonNode?

    /**
     * Delete
     */
    fun delete(entity: ProjectEntity, key: String)

    /**
     * Flexible query based on JSON, to get a first match based on numeric comparison
     */
    fun findFirstJsonFieldGreaterOrEqual(type: ProjectEntityType, reference: Pair<String, Int>, value: Long, vararg jsonPath: String): Int?

}
