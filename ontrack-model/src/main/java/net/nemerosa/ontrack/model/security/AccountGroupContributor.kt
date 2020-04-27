package net.nemerosa.ontrack.model.security

import org.springframework.stereotype.Component

/**
 * Contributes a list of groups to an account.
 */
interface AccountGroupContributor {

    /**
     * Collects the list of groups for this account
     *
     * @param account Account
     * @return List of groups
     */
    fun collectGroups(account: Account): Collection<AccountGroup>

}

/**
 * NOP contributor
 */
@Component
class NOPAccountGroupContributor : AccountGroupContributor {
    override fun collectGroups(account: Account): Collection<AccountGroup> = emptySet()
}
