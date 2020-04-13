package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.OntrackUser
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.repository.BuiltinAccount
import org.springframework.security.core.CredentialsContainer
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
            override val accountId: Int,
            private val username: String,
            private var password: String,
            private val role: String
    ) : OntrackUser, CredentialsContainer {

        constructor(account: BuiltinAccount) : this(
                accountId = account.id,
                username = account.name,
                password = account.password,
                role = account.role.roleName
        )

        override fun getUsername(): String = username

        override fun getPassword(): String = password

        override fun getAuthorities(): List<GrantedAuthority> = AuthorityUtils.createAuthorityList(role)

        override fun isEnabled(): Boolean = true

        override fun isCredentialsNonExpired(): Boolean = true

        override fun isAccountNonExpired(): Boolean = true

        override fun isAccountNonLocked(): Boolean = true

        override fun eraseCredentials() {
            password = ""
        }
    }

}