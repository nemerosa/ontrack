package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.AuthenticationSource

/**
 * Management of and access to the groups which are provided
 * to an account by an external authentication system.
 */
interface ProvidedGroupsRepository {

    /**
     * Saves a list of provided groups for an account
     * given an [authentication source][AuthenticationSource].
     *
     * @param account ID of the account
     * @param source Authentication source
     * @param groups Names of the groups provided by the authentication source
     */
    fun saveProvidedGroups(account: Int, source: AuthenticationSource, groups: Set<String>)

    /**
     * Gets the list of provided groups for an account
     * given an [authentication source][AuthenticationSource].
     *
     * @param account ID of the account
     * @param source Authentication source
     * @return Names of the groups provided by the authentication source
     */
    fun getProvidedGroups(account: Int, source: AuthenticationSource): Set<String>

}