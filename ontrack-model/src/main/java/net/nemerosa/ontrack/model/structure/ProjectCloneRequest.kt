package net.nemerosa.ontrack.model.structure;

/**
 * Request for the clone of a project into another one.
 */
data class ProjectCloneRequest(
    val name: String,
    val sourceBranchId: ID,
    override val replacements: List<Replacement>,
) : CopyRequest
