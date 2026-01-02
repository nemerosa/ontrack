package net.nemerosa.ontrack.model.structure;

/**
 * Request for the bulk update of a branch.
 */
data class BranchBulkUpdateRequest(
    override val replacements: List<Replacement>,
) : CopyRequest
