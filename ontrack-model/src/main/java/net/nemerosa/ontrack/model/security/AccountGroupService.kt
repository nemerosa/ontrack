package net.nemerosa.ontrack.model.security

interface AccountGroupService {

    /**
     * Gets the assigned groups & mapped groups for a given account.
     */
    fun getAccountGroups(account: Account): AccountGroups

}