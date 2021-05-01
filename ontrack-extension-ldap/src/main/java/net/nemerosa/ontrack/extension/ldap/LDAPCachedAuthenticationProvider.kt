package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.security.authentication.AuthenticationServiceException
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
        private val applicationLogService: ApplicationLogService,
        private val providedGroupsService: ProvidedGroupsService
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
                val userDetails: ExtendedLDAPUserDetails = if (principal is ExtendedLDAPUserDetails) {
                    principal
                } else {
                    throw LDAPMissingDetailsException()
                }
                // If not found, auto-registers the account using the LDAP details
                getOrCreateAccount(name, userDetails)
            } else {
                null
            }
        }
    }

    private fun getOrCreateAccount(username: String, userDetails: ExtendedLDAPUserDetails): OntrackAuthenticatedUser? {
        // Gets an existing account using user name only
        val existingAccount = securityService.asAdmin { accountService.findAccountByName(username) }
        return if (existingAccount == null) {
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
                                    groups = emptyList(),
                                    disabled = false,
                                    locked = false,
                            ),
                            LDAPAuthenticationSourceProvider.SOURCE
                    )
                }
                createOntrackAuthenticatedUser(account, userDetails)
            } else {
                throw LDAPEmailRequiredException()
            }
        } else {
            // Checks the source
            if (existingAccount.authenticationSource == LDAPAuthenticationSourceProvider.SOURCE) {
                createOntrackAuthenticatedUser(existingAccount, userDetails)
            } else {
                throw LDAPNotALDAPAccountException(username)
            }
        }
    }

    private fun createOntrackAuthenticatedUser(account: Account, userDetails: ExtendedLDAPUserDetails): OntrackAuthenticatedUser {
        // Wrapping the account
        val user = AccountOntrackUser(account)
        // Gets the groups provided by the LDAP
        val groups = userDetails.groups.toSet()
        // Registers the groups
        securityService.asAdmin {
            providedGroupsService.saveProvidedGroups(account.id(), LDAPAuthenticationSourceProvider.SOURCE, groups)
        }
        // Provides the ACL
        return accountService.withACL(user)
    }

    override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) {
    }

    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails =
            findUser(username, authentication)
                    ?: throw AuthenticationServiceException("Cannot authenticate against LDAP")

}