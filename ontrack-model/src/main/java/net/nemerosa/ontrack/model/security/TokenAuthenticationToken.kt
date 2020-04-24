package net.nemerosa.ontrack.model.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.DigestUtils

class TokenAuthenticationToken
private constructor(
        private var token: String,
        private val encodedToken: String,
        authorities: Collection<GrantedAuthority>,
        private val principal: Any
) : AbstractAuthenticationToken(authorities) {

    /**
     * Constructor used by filters.
     */
    constructor(token: String) : this(
            token = token,
            encodedToken = "",
            authorities = emptyList(),
            principal = token
    )

    /**
     * Constructor after authentication
     */
    constructor(token: String, authorities: Collection<GrantedAuthority>, details: UserDetails) : this(
            token = "",
            encodedToken = encode(token),
            authorities = authorities,
            principal = details
    ) {
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any = token

    override fun getPrincipal(): Any = principal

    override fun eraseCredentials() {
        super.eraseCredentials()
        token = ""
    }

    fun matches(token: String): Boolean = encodedToken == encode(token)

    companion object {
        private fun encode(token: String) = DigestUtils.md5DigestAsHex(token.toByteArray())
    }
}