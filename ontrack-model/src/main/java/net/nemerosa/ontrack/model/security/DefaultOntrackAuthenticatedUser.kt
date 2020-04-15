package net.nemerosa.ontrack.model.security

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.userdetails.UserDetails

class DefaultOntrackAuthenticatedUser(
        override val user: OntrackUser,
        val authorizedAccount: AuthorizedAccount,
        val groups: List<AuthorizedGroup>
) : OntrackAuthenticatedUser, UserDetails by user, CredentialsContainer {

    override val account: Account = authorizedAccount.account

    override fun isGranted(fn: Class<out GlobalFunction>) =
            authorizedAccount.isGranted(fn) || groups.any { it.isGranted(fn) }

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>) =
            authorizedAccount.isGranted(projectId, fn) || groups.any { it.isGranted(projectId, fn) }

    override fun eraseCredentials() {
        if (user is CredentialsContainer) {
            user.eraseCredentials()
        }
    }
}
