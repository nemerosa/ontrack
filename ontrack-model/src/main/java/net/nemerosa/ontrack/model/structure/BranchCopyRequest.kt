package net.nemerosa.ontrack.model.structure;


/**
 * Request for the copy of a branch configuration into another one.
 */
data class BranchCopyRequest(
    val sourceBranchId: ID,
    override val replacements: List<Replacement>
) : CopyRequest
