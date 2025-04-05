package net.nemerosa.ontrack.model.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class AuthenticatedUserAuthentication(
    val authenticatedUser: AuthenticatedUser,
    authorities: Collection<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials(): Any = ""

    override fun getPrincipal(): Any = authenticatedUser

    override fun isAuthenticated(): Boolean = true
}