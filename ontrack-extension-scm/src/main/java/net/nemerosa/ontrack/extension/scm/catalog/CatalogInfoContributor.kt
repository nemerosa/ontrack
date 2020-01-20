package net.nemerosa.ontrack.extension.scm.catalog

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Project

/**
 * Extension used to collect information about a SCM catalog entry and a project.
 *
 * @param T Type of information being collected
 */
interface CatalogInfoContributor<T> : Extension {

    fun collectInfo(project: Project, entry: SCMCatalogEntry): T?

    /**
     * Converts an object from the model to a JSON representation, for storage
     */
    fun asStoredJson(info: T): JsonNode

    /**
     * Converts a stored JSON to the model
     *
     * @param project Project for which the catalog info is reloaded
     * @param node Stored JSON
     * @return Model object or `null` if the stored JSON cannot be parsed into a valid representation.
     */
    fun fromStoredJson(project: Project, node: JsonNode): T?

    /**
     * Converts an object from the model to a JSON representation, for client usage
     */
    fun asClientJson(info: T): JsonNode

    /**
     * ID of this contributor
     */
    val id: String get() = javaClass.name

    /**
     * Display name for this contributor
     */
    val name: String

}
