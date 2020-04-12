package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.OntrackUser
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.repository.BuiltinAccount
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Reads users from the `ACCOUNTS` table.
 *
 * @see SecurityConfiguration
 */
class BuiltinUserDetailsService(
        private val accountService: AccountService,
        private val accountRepository: AccountRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = accountRepository.findBuiltinAccount(username)
                ?: throw UsernameNotFoundException("Cannot find built-in user with user name = $username")
        return accountService.withACL(BuiltinOntrackUser(user))
    }

    class BuiltinOntrackUser(
            private val account: BuiltinAccount
    ) : OntrackUser {

        override val accountId: Int = account.id

        override fun getUsername(): String = account.name

        override fun getPassword(): String = account.password

        override fun getAuthorities(): List<GrantedAuthority> = AuthorityUtils.createAuthorityList(account.role.roleName)

        override fun isEnabled(): Boolean = true

        override fun isCredentialsNonExpired(): Boolean = false

        override fun isAccountNonExpired(): Boolean = false

        override fun isAccountNonLocked(): Boolean = false

    }

}