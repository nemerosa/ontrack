package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.model.security.*
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser

class OntrackOidcUserService(
        private val accountService: AccountService,
        private val securityService: SecurityService,
        private val providedGroupsService: ProvidedGroupsService
) : OidcUserService() {

    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val oidcUser: OidcUser = super.loadUser(userRequest)
        val authenticationSource = OidcAuthenticationSourceProvider.asSource(userRequest)
        // Gets the user name (as email)
        val email: String? = oidcUser.userInfo.email
        if (email.isNullOrBlank()) {
            throw OidcEmailRequiredException()
        }
        // Full name
        val fullName = oidcUser.fullName ?: email
        // Gets the account
        val existingAccount = securityService.asAdmin { accountService.findAccountByName(email) }
        // Creates the account if not existing
        return if (existingAccount == null) {
            // Registration
            val account: Account = securityService.asAdmin {
                // Creates the account
                accountService.create(
                        AccountInput(
                                name = email,
                                fullName = fullName,
                                email = email,
                                password = "",
                                groups = emptyList()
                        ),
                        authenticationSource
                )
            }
            createOntrackAuthenticatedUser(account, oidcUser, authenticationSource)
        } else {
            // Checks the source
            if (existingAccount.authenticationSource sameThan authenticationSource) {
                createOntrackAuthenticatedUser(existingAccount, oidcUser, authenticationSource)
            } else {
                throw OidcNonOidcExistingUserException(email)
            }
        }
    }

    private fun createOntrackAuthenticatedUser(account: Account, oidcUser: OidcUser, authenticationSource: AuthenticationSource): OidcUser {
        // Wrapping the account
        val ontrackUser = AccountOntrackUser(account)
        // Gets the groups provided by OIDC
        val groups = oidcUser.getClaimAsStringList("groups").toSet()
        // Registers the groups
        securityService.asAdmin {
            providedGroupsService.saveProvidedGroups(account.id(), authenticationSource, groups)
        }
        // Provides the ACL
        val ontrackAuthenticatedUser = accountService.withACL(ontrackUser)
        // Wrapping everything together
        return OntrackOidcUser(oidcUser, ontrackAuthenticatedUser)
    }
}

class OidcEmailRequiredException : AuthenticationException("The user email is required as part of the OIDC user information.")

class OidcNonOidcExistingUserException(email: String) : AuthenticationException("The user with email `$email` already exists and is not an OIDC user.")
