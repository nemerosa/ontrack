package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthenticationUserServiceImpl(
    private val accountACLService: AccountACLService,
) : AuthenticationUserService {

    override fun asUser(account: Account) {
        val enrichedAuth = AuthenticatedUserAuthentication(
            authenticatedUser = createAuthenticatedUser(account),
            authorities = AuthorityUtils.createAuthorityList(SecurityRole.USER.name)
        )
        SecurityContextHolder.getContext().authentication = enrichedAuth
    }


    private fun createAuthenticatedUser(account: Account): AccountAuthenticatedUser {
        return AccountAuthenticatedUser(
            account = account,
            authorisations = accountACLService.getAuthorizations(account),
            groups = accountACLService.getGroups(account),
        )
    }
}