package net.nemerosa.ontrack.model.security

import org.springframework.stereotype.Component

/**
 * [AccountGroupContributor] based on a list of provided groups by an external system.
 *
 * @param providedGroupsService Service used to access the provided group names
 * @param accountGroupMappingService Mapping between groups
 * @see ProvidedGroupsService
 */
@Component
class ProvidedGroupAccountGroupContributor(
        private val providedGroupsService: ProvidedGroupsService,
        private val accountGroupMappingService: AccountGroupMappingService
) : AccountGroupContributor {

    override fun collectGroups(account: Account): Collection<AccountGroup> {
        // Gets the authentication source of this account
        val authenticationSource = account.authenticationSource
        return if (authenticationSource.isEnabled && authenticationSource.isGroupMappingSupported) {
            // Gets the list of provided groups for this account and the associated authentication source
            val groupNames = providedGroupsService.getProvidedGroups(account.id(), authenticationSource)
            // Maps the group names to actual groups
            groupNames.map { groupName ->
                accountGroupMappingService.getGroups(authenticationSource, groupName)
            }.flatten()
        } else {
            emptyList()
        }
    }
}