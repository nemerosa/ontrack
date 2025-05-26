package net.nemerosa.ontrack.repository

interface AccountIdpGroupRepository {

    /**
     * Synchronizing the incoming groups with the existing ones
     */
    fun syncGroups(accountId: Int, idpGroups: List<String>)

    /**
     * Gets the list of groups associated with an account
     */
    fun getAccountIdpGroups(accountId: Int): List<String>

}