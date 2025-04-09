package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.SecurityRole
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils

class TokenAuthenticationToken(
    val token: String,
    val account: Account,
) : AbstractAuthenticationToken(
    AuthorityUtils.createAuthorityList(SecurityRole.USER.name)
) {
    override fun getCredentials(): Any = token

    override fun getPrincipal(): Any = account

    override fun isAuthenticated(): Boolean = true
}
