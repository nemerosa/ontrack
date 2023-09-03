package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Declaration of authorization on a domain for a given action")
data class Authorization(
    @APIDescription("Domain of the authorization on the target context.")
    val name: String,
    @APIDescription("Action to perform")
    val action: String,
    @APIDescription("Authorization to perform the action on the domain")
    val authorized: Boolean,
) {

    companion object {

        /**
         * Common actions: creating
         */
        const val CREATE = "create"

        /**
         * Common actions: editing
         */
        const val EDIT = "edit"

        /**
         * Common actions: sharing
         */
        const val SHARE = "share"

    }

}
