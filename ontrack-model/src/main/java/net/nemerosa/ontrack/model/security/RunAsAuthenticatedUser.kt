package net.nemerosa.ontrack.model.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils

class RunAsAuthenticatedUser private constructor(
    private val authenticatedUser: AuthenticatedUser?,
) : AuthenticatedUser {

    override val account: Account? = authenticatedUser?.account

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean = true

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = true

    override fun getName(): String = authenticatedUser?.name ?: "admin"

    override val groups: List<AuthorizedGroup> =
        authenticatedUser?.groups ?: emptyList()

    companion object {

        fun runAsUser(authenticatedUser: AuthenticatedUser?) = RunAsAuthenticatedUser(
            authenticatedUser = authenticatedUser
        )

        fun authentication(authenticatedUser: AuthenticatedUser?): Authentication {
            return AuthenticatedUserAuthentication(
                authenticatedUser = runAsUser(authenticatedUser),
                authorities = AuthorityUtils.createAuthorityList(SecurityRole.ADMINISTRATOR.name)
            )
        }

    }

}