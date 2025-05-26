package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.TransientSecurityContext
import org.springframework.stereotype.Service

@Service
class AccountSecurityContextServiceImpl(
    private val authenticationUserService: AuthenticationUserService,
) : AccountSecurityContextService {
    override fun withAccount(account: Account, code: () -> Unit) {
        val user = authenticationUserService.createAuthenticatedUser(account)
        val authentication = AuthenticatedUserAuthentication(
            authenticatedUser = user,
            authorities = AuthorityUtils.createAuthorityList(SecurityRole.USER.name),
        )
        val oldSecurityContext = SecurityContextHolder.getContext()
        val securityContext = TransientSecurityContext(authentication)
        try {
            SecurityContextHolder.setContext(securityContext)
            code()
        } finally {
            SecurityContextHolder.setContext(oldSecurityContext)
        }
    }
}