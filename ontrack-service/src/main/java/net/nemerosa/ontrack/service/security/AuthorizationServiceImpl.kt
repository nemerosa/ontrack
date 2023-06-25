package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.security.AuthorizationService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service

@Service
class AuthorizationServiceImpl(
    private val securityService: SecurityService,
    private val authorizationContributors: List<AuthorizationContributor>,
) : AuthorizationService {
    override val authorizations: List<Authorization>
        get() {
            val user = securityService.currentAccount ?: return emptyList()
            return authorizationContributors.flatMap {
                it.getAuthorizations(user)
            }
        }
}