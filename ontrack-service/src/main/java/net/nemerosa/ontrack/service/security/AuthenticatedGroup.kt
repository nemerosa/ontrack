package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.Authorisations

class AuthenticatedGroup(
        val group: AccountGroup,
        val authorisations: Authorisations
)
