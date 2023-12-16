package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class CoreAuthorizationContributor : AuthorizationContributor {

    companion object {
        const val PROJECT = "project"
        const val USER = "user"
    }

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        listOf(
            // User
            Authorization(
                USER,
                "changePassword",
                !user.account.locked && user.account.authenticationSource.isAllowingPasswordChange
            ),
            // Project
            Authorization(
                PROJECT,
                Authorization.CREATE,
                user.isGranted(ProjectCreation::class.java)
            ),
        )

}