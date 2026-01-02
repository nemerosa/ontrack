package net.nemerosa.ontrack.model.structure;

/**
 * Request for the clone of a branch into another one.
 */
data class BranchCloneRequest(
    val name: String,
    override val replacements: kotlin.collections.List<Replacement>,
) : CopyRequest
