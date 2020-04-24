package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.OntrackUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

/**
 * [Account] based [OntrackUser].
 */
open class AccountOntrackUser(
        private val account: Account,
        private val credentialNonExpired: Boolean = true
) : OntrackUser {

    override val accountId: Int = account.id()

    override fun getAuthorities(): Collection<GrantedAuthority> = AuthorityUtils.createAuthorityList(account.role.roleName)

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String = account.name

    override fun isCredentialsNonExpired(): Boolean = credentialNonExpired

    override fun getPassword(): String = ""

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true
}