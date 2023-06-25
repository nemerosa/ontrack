package net.nemerosa.ontrack.model.security

interface AuthorizationContributor {

    /**
     * Gets the list of authorizations for the current user
     */
    fun getAuthorizations(user: OntrackAuthenticatedUser): List<Authorization>

}