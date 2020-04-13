package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.userdetails.UserDetails

class DefaultOntrackAuthenticatedUser(
        override val user: OntrackUser,
        override val account: Account,
        val authorisations: Authorisations,
        val groups: List<AuthenticatedGroup>
) : OntrackAuthenticatedUser, UserDetails by user, CredentialsContainer {

    override fun isGranted(fn: Class<out GlobalFunction>) =
            (SecurityRole.ADMINISTRATOR == account.role)
                    || groups.any { it.isGranted(fn) }
                    || authorisations.isGranted(fn)

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>) =
            SecurityRole.ADMINISTRATOR == account.role
                    || groups.any { it.isGranted(projectId, fn) }
                    || authorisations.isGranted(projectId, fn)

    override fun eraseCredentials() {
        if (user is CredentialsContainer) {
            user.eraseCredentials()
        }
    }
}
