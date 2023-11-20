package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser

/**
 * Extension which contributes a list of authorizations.
 */
interface AuthorizationContributorExtension : Extension {

    /**
     * Gets the list of authorizations for the current user and the given [context]
     */
    fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization>

}