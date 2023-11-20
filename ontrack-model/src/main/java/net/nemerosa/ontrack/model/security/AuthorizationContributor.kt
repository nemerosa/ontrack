package net.nemerosa.ontrack.model.security

interface AuthorizationContributor {

    /**
     * Does this contributor applies to a given context
     */
    fun appliesTo(context: Any): Boolean

    /**
     * Gets the list of authorizations for the current user and the given [context]
     */
    fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization>

}