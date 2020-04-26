package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider
import org.springframework.stereotype.Component

/**
 * Facade for a [LdapAuthenticationProvider], which might be present or not, depending
 * on what is returned by [LDAPProviderFactory].
 */
@Component
class LDAPCachedAuthenticationProvider(
        private val accountService: AccountService,
        private val ldapProviderFactory: LDAPProviderFactory,
        private val securityService: SecurityService,
        private val applicationLogService: ApplicationLogService
) : AbstractUserDetailsAuthenticationProvider() {

    internal fun findUser(username: String, authentication: UsernamePasswordAuthenticationToken): OntrackAuthenticatedUser? {
        // Gets the (cached) provider
        val ldapAuthenticationProvider: LdapAuthenticationProvider? = ldapProviderFactory.provider
        // If not enabled, cannot authenticate!
        return if (ldapAuthenticationProvider == null) {
            null
        } else {
            val ldapAuthentication: Authentication? = try {
                ldapAuthenticationProvider.authenticate(authentication)
            } catch (ex: Exception) {
                // Cannot use the LDAP, logs the error
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
                val principal = ldapAuthentication.principal
                val userDetails: ExtendedLDAPUserDetails? = if (principal is ExtendedLDAPUserDetails) {
                    principal
                } else {
                    null
                }
                // If not found, auto-registers the account using the LDAP details
                getOrCreateAccount(name, userDetails)
            } else {
                null
            }
        }
    }

    private fun getOrCreateAccount(username: String, userDetails: ExtendedLDAPUserDetails?): OntrackAuthenticatedUser? {
        // Gets an existing account using user name only
        val existingAccount = securityService.asAdmin { accountService.findAccountByName(username) }
        return if (existingAccount == null) {
            // If found, auto-registers the account using the LDAP details
            if (userDetails != null) {
                // Auto-registration if email is OK
                if (userDetails.email.isNotBlank()) {
                    // Registration
                    val account = securityService.asAdmin {
                        // Creates the account
                        accountService.create(
                                AccountInput(
                                        name = username,
                                        fullName = userDetails.fullName,
                                        email = userDetails.email,
                                        password = "",
                                        groups = emptyList()
                                ),
                                LDAPAuthenticationSource.id
                        )
                    }
                    // Wrapping the account
                    val user = AccountOntrackUser(account)
                    // Provides the ACL
                    accountService.withACL(user)
                } else {
                    throw LDAPEmailRequiredException()
                }
            } else {
                throw LDAPMissingDetailsException()
            }
        } else {
            // Checks the source
            if (existingAccount.authenticationSource == LDAPAuthenticationSource) {
                // Wrapping the account
                val user = AccountOntrackUser(existingAccount)
                // Provides the ACL
                accountService.withACL(user)
            } else {
                throw LDAPNotALDAPAccountException(username)
            }
        }
    }

    override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) {
    }

    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails? =
            findUser(username, authentication)

}