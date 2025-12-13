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

    override fun getAuthorizations(context: Any): List<Authorization> {
        val user = securityService.currentUser ?: return emptyList()
        return authorizationContributors
            .filter { it.appliesTo(context) }
            .flatMap { it.getAuthorizations(user, context) }
    }
}