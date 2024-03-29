package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class CoreAuthorizationContributor : AuthorizationContributor {

    companion object {
        const val GLOBAL = "global"
        const val USER = "user"
        const val PROJECT = "project"
        const val PROMOTION_LEVEL = "promotion_level"
    }

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        listOf(
            // Global settings
            Authorization(
                GLOBAL,
                Authorization.SETTINGS,
                user.isGranted(GlobalSettings::class.java)
            ),
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
            // Promotion levels
            Authorization(
                PROMOTION_LEVEL,
                "bulkUpdate",
                user.isGranted(GlobalSettings::class.java)
            ),
        )

}