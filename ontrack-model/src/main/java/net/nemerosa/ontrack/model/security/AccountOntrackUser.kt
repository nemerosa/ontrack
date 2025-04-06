package net.nemerosa.ontrack.model.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

/**
 * [Account] based [OntrackUser].
 */
@Deprecated("Will be removed in V5")
open class AccountOntrackUser(
        private val account: Account
) : OntrackUser {

    override val accountId: Int = account.id()

    override fun getAuthorities(): Collection<GrantedAuthority> = AuthorityUtils.createAuthorityList(account.role.roleName)

    override fun isEnabled(): Boolean = !account.disabled && account.authenticationSource.isEnabled

    override fun getUsername(): String = account.name

    override fun isCredentialsNonExpired(): Boolean = true

    override fun getPassword(): String = ""

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true
}