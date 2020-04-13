package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.Authorisations
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import net.nemerosa.ontrack.model.security.OntrackUser
import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.userdetails.UserDetails

class DefaultOntrackAuthenticatedUser(
        override val user: OntrackUser,
        override val account: Account,
        val authorisations: Authorisations,
        val groups: List<AuthenticatedGroup>
) : OntrackAuthenticatedUser, UserDetails by user, CredentialsContainer {
    override fun eraseCredentials() {
        if (user is CredentialsContainer) {
            user.eraseCredentials()
        }
    }
}
