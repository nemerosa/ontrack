package net.nemerosa.ontrack.model.security

/**
 * [AccountGroupContributor] based on a list of provided groups by an external system.
 *
 * @param providedGroupsService Service used to access the provided group names
 * @param authenticationSource Authentication source used by this service
 * @param accountGroupMappingService Mapping between groups
 * @see ProvidedGroupsService
 */
abstract class AbstractProvidedGroupAccountGroupContributor(
        private val providedGroupsService: ProvidedGroupsService,
        private val authenticationSource: AuthenticationSource,
        private val accountGroupMappingService: AccountGroupMappingService
) : AccountGroupContributor {

    override fun collectGroups(account: Account): Collection<AccountGroup> {
        // Gets the list of provided groups for this account and the associated authentication source
        val groupNames = providedGroupsService.getProvidedGroups(account.id(), authenticationSource)
        // Maps the group names to actual groups
        return groupNames.map { groupName ->
            accountGroupMappingService.getGroups(authenticationSource, groupName)
        }.flatten()
    }
}