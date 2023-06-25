package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser

/**
 * Extension which contributes a list of authorizations.
 */
interface AuthorizationContributorExtension : Extension {

    /**
     * Gets the list of authorizations for the current user
     */
    fun getAuthorizations(user: OntrackAuthenticatedUser): List<Authorization>

}