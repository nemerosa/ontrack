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
     * Converts an object from the model to a JSON representation
     */
    fun asJson(info: T): JsonNode

    /**
     * Converts a stored JSON to the model
     */
    fun fromJson(node: JsonNode): T

    /**
     * ID of this contributor
     */
    val id: String get() = javaClass.name

}
