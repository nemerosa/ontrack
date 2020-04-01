package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
@Qualifier("ldap")
class LDAPAuthenticationProvider(
        private val accountService: AccountService,
        private val ldapProviderFactory: LDAPProviderFactory,
        private val ldapAuthenticationSourceProvider: LDAPAuthenticationSourceProvider,
        private val securityService: SecurityService,
        private val applicationLogService: ApplicationLogService
) : AbstractUserDetailsAuthenticationProvider(), UserSource {

    private val cache: MutableMap<String, AccountUserDetails> = ConcurrentHashMap()

    internal fun findUser(username: String, authentication: UsernamePasswordAuthenticationToken): AuthenticatedAccount? {
        // Gets the (cached) provider
        val ldapAuthenticationProvider = ldapProviderFactory.provider
        // If not enabled, cannot authenticate!
        return if (ldapAuthenticationProvider == null) {
            null
        } else {
            val ldapAuthentication: Authentication? = try {
                ldapAuthenticationProvider.authenticate(authentication)
            } catch (ex: Exception) { // Cannot use the LDAP, logs the error
                applicationLogService.log(
                        ApplicationLogEntry.error(
                                ex,
                                NameDescription.nd(
                                        "ldap-authentication",
                                        "LDAP Authentication problem"
                                ),
                                authentication.name
                        )
                )
                // Rejects the authentication
                null
            }
            if (ldapAuthentication != null && ldapAuthentication.isAuthenticated) {
                // Gets the account name
                val name = ldapAuthentication.name
                // If not found, auto-registers the account using the LDAP details
                val userDetails: ExtendedLDAPUserDetails?
                val principal = ldapAuthentication.principal
                userDetails = if (principal is ExtendedLDAPUserDetails) {
                    principal
                } else {
                    null
                }
                // Gets any existing account
                val existingAccount = securityService.asAdmin<Account?> { accountService.findUserByNameAndSource(username, ldapAuthenticationSourceProvider).getOrNull() }
                if (existingAccount == null) {
                    // If not found, auto-registers the account using the LDAP details
                    if (userDetails != null) {
                        // Auto-registration if email is OK
                        if (StringUtils.isNotBlank(userDetails.email)) {
                            // Registration
                            securityService.callAsAdmin {
                                AuthenticatedAccount(
                                        accountService.create(
                                                AccountInput(
                                                        name,
                                                        userDetails.fullName,
                                                        userDetails.email,
                                                        "", emptyList()),
                                                LDAPAuthenticationSourceProvider.LDAP_AUTHENTICATION_SOURCE
                                        ),
                                        userDetails
                                )
                            }
                        } else {
                            // Temporary account
                            AuthenticatedAccount.of(
                                    Account.of(
                                            name,
                                            userDetails.fullName,
                                            "",
                                            SecurityRole.USER,
                                            ldapAuthenticationSourceProvider.source
                                    )
                            )
                        }
                    } else {
                        // Temporary account
                        AuthenticatedAccount.of(
                                Account.of(
                                        name,
                                        name,
                                        "",
                                        SecurityRole.USER,
                                        ldapAuthenticationSourceProvider.source
                                )
                        )
                    }
                } else {
                    AuthenticatedAccount(existingAccount, userDetails ?: AccountUserDetails(existingAccount))
                }
            } else {
                null
            }
        }
    }

    @Throws(AuthenticationException::class)
    override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) {
    }

    @Throws(AuthenticationException::class)
    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails {
        val userDetails = findUser(username, authentication)
                ?.let { accountService.withACL(it) }
                ?.let { AccountUserDetails(it) }
                ?: throw UsernameNotFoundException("User $username cannot be found")
        cache[username] = userDetails
        return userDetails
    }

    override fun loadUser(username: String): Optional<AccountUserDetails> {
        return Optional.ofNullable(cache[username])
    }

    override fun onLogout(username: String) {
        cache.remove(username)
    }

}