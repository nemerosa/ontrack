package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.model.security.*
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.oidc.user.OidcUser

class OntrackOidcUserService(
    private val accountService: AccountService,
    private val securityService: SecurityService,
    private val providedGroupsService: ProvidedGroupsService,
    private val oidcSettingsService: OIDCSettingsService,
) : OidcUserService() {

    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val oidcUser: OidcUser = super.loadUser(userRequest)
        val clientRegistration: ClientRegistration = userRequest.clientRegistration
        val wrappedOntrackClientRegistration = WrappedOntrackClientRegistration(clientRegistration)
        return linkOidcUser(wrappedOntrackClientRegistration, oidcUser)
    }

    internal fun linkOidcUser(clientRegistration: OntrackClientRegistration, oidcUser: OidcUser): OidcUser {
        val authenticationSource = OidcAuthenticationSourceProvider.asSource(clientRegistration)
        // Gets the user name (as email)
        val email: String? = oidcUser.userInfo?.email ?: oidcUser.email
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
                        groups = emptyList(),
                        disabled = false,
                        locked = false,
                    ),
                    authenticationSource
                )
            }
            createOntrackAuthenticatedUser(account, oidcUser, clientRegistration, authenticationSource)
        } else {
            // Checks the source
            if (existingAccount.authenticationSource sameThan authenticationSource) {
                createOntrackAuthenticatedUser(existingAccount, oidcUser, clientRegistration, authenticationSource)
            } else {
                throw OidcNonOidcExistingUserException(email)
            }
        }
    }

    private fun createOntrackAuthenticatedUser(
        account: Account,
        oidcUser: OidcUser,
        clientRegistration: OntrackClientRegistration,
        authenticationSource: AuthenticationSource,
    ): OidcUser {
        // If the account is disabled, fails the authentication
        if (account.disabled) {
            throw DisabledException("Account is disabled.")
        }
        // Wrapping the account
        val ontrackUser = AccountOntrackUser(account)
        // Filter on the groups
        val groupFilter = securityService.asAdmin {
            oidcSettingsService
                .getProviderById(clientRegistration.registrationId)
                ?.groupFilter
                ?: ".*"
        }
        val groupFilterRegex = groupFilter.toRegex(RegexOption.IGNORE_CASE)
        // Gets the groups provided by OIDC
        val groups = oidcUser.getClaimAsStringList("groups")
            ?.filter { groupFilterRegex.matches(it) }
            ?.toSet()
            ?: emptySet()
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

class OidcEmailRequiredException :
    AuthenticationException("The user email is required as part of the OIDC user information.")

class OidcNonOidcExistingUserException(email: String) :
    AuthenticationException("The user with email `$email` already exists and is not an OIDC user.")
