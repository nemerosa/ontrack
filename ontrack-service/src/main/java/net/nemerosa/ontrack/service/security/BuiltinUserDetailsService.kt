package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.repository.AccountRepository
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Reads users from the `ACCOUNTS` table.
 *
 * @see SecurityConfiguration
 */
class BuiltinUserDetailsService(
        private val accountRepository: AccountRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = accountRepository.findBuiltinAccount(username)
                ?: throw UsernameNotFoundException("Cannot find built-in user with user name = $username")
        return User(
                user.name,
                user.password,
                true,
                true,
                true,
                true,
                AuthorityUtils.createAuthorityList(user.role.roleName)
        )
    }

}