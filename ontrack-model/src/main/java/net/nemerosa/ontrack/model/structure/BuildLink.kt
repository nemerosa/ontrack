package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Qualified build link.
 *
 * @property build Build targeted by the link
 * @property qualifier Link qualifier (can be empty but not null)
 */
data class BuildLink(
    @APIDescription("Linked build")
    val build: Build,
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
