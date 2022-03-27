package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Type and ID")
data class ProjectEntityID(
    @APIDescription("Project entity type")
    val type: ProjectEntityType,
    @APIDescription("Project entity ID")
    val id: Int,
) {
    constructor(projectEntity: ProjectEntity) : this(
        type = projectEntity.projectEntityType,
        id = projectEntity.id(),
    )
}