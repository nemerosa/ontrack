package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.model.security.*
import org.springframework.security.oauth2.core.oidc.user.OidcUser

class OntrackOidcUser(
        private val base: OidcUser,
        private val ontrackAuthenticatedUser: OntrackAuthenticatedUser
) : OidcUser by base, OntrackAuthenticatedUser {

    override val user: OntrackUser = ontrackAuthenticatedUser.user
    override val account: Account = ontrackAuthenticatedUser.account

    override fun isEnabled(): Boolean = ontrackAuthenticatedUser.isEnabled

    override fun getUsername(): String = ontrackAuthenticatedUser.username

    override fun isCredentialsNonExpired(): Boolean = ontrackAuthenticatedUser.isCredentialsNonExpired

    override fun getPassword(): String = ""

    override fun isAccountNonExpired(): Boolean = ontrackAuthenticatedUser.isAccountNonExpired

    override fun isAccountNonLocked(): Boolean = ontrackAuthenticatedUser.isAccountNonLocked

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean =
            ontrackAuthenticatedUser.isGranted(fn)

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean =
            ontrackAuthenticatedUser.isGranted(projectId, fn)
}
