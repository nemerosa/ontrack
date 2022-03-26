package net.nemerosa.ontrack.model.structure

data class ProjectEntityID(
    val type: ProjectEntityType,
    val id: Int,
) {
    constructor(projectEntity: ProjectEntity) : this(
        type = projectEntity.projectEntityType,
        id = projectEntity.id(),
    )
}