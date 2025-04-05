package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.security.AuthenticatedUser
import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.security.GlobalAuthorizationContext
import org.springframework.stereotype.Component

@Component
class DashboardAuthorizationContributor : AuthorizationContributor {

    companion object {
        private const val DASHBOARD = "dashboard"
    }

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: AuthenticatedUser, context: Any): List<Authorization> = listOf(
        Authorization(DASHBOARD, Authorization.EDIT, user.isGranted(DashboardEdition::class.java)),
        Authorization(DASHBOARD, Authorization.SHARE, user.isGranted(DashboardSharing::class.java)),
    )

}