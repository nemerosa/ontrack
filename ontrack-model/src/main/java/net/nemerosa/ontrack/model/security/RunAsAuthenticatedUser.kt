package net.nemerosa.ontrack.model.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils

class RunAsAuthenticatedUser(
    private val authenticatedUser: AuthenticatedUser?,
) : AuthenticatedUser {

    override val account: Account? = authenticatedUser?.account

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean = true

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = true

    override fun getName(): String = authenticatedUser?.name ?: "RunAsAdmin"

    companion object {

        fun authentication(authenticatedUser: AuthenticatedUser?): Authentication? {
            return AuthenticatedUserAuthentication(
                authenticatedUser = RunAsAuthenticatedUser(
                    authenticatedUser = authenticatedUser
                ),
                authorities = AuthorityUtils.createAuthorityList(SecurityRole.ADMINISTRATOR.name)
            )
        }

    }

}