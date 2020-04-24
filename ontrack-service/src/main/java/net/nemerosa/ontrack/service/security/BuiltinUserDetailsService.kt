package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.repository.BuiltinAccount
import org.springframework.security.core.CredentialsContainer
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
            private val buildinAccount: BuiltinAccount
    ) : AccountOntrackUser(buildinAccount.account), CredentialsContainer {

        override fun getPassword(): String = buildinAccount.password

        override fun eraseCredentials() {
            buildinAccount.password = ""
        }
    }

}