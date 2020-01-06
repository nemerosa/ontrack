package net.nemerosa.ontrack.model.structure

/**
 * Identification of a [ProjectEntity] using its [type][ProjectEntityType] and its [ID].
 */
data class ProjectEntityID(
        val type: ProjectEntityType,
        val id: ID
)
