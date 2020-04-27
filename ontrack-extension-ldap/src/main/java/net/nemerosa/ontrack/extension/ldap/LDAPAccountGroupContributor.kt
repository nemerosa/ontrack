package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AbstractProvidedGroupAccountGroupContributor
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.model.security.ProvidedGroupsService
import org.springframework.stereotype.Component

@Component
class LDAPAccountGroupContributor(
        providedGroupsService: ProvidedGroupsService,
        accountGroupMappingService: AccountGroupMappingService
) : AbstractProvidedGroupAccountGroupContributor(
        providedGroupsService,
        LDAPAuthenticationSourceProvider.SOURCE,
        accountGroupMappingService
)
