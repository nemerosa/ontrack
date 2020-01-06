package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityID
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
     * Looks for a linked entry using a JSON value
     */
    fun findEntityByValue(type: ProjectEntityType, key: String, value: JsonNode): ProjectEntityID?

    /**
     * Delete
     */
    fun delete(entity: ProjectEntity, key: String)

}
