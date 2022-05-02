package net.nemerosa.ontrack.model.structure

/**
 * Converts a project entity into a project entity ID.
 */
fun ProjectEntity.toProjectEntityID() = ProjectEntityID(this)
