package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Qualified branch link.
 *
 * @property branch Branch targeted by the link
 * @property qualifier Link qualifier (can be empty but not null)
 */
data class BranchLink(
    @APIDescription("Linked branch")
    val branch: Branch,
    @APIDescription("Nature/qualification of the link")
    val qualifier: String,
) {
    companion object {
        /**
         * Default qualifier value for unqualified links.
         */
        const val DEFAULT = ""
    }
}
