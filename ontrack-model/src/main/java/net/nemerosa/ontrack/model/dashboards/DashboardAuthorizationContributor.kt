package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import org.springframework.stereotype.Component

@Component
class DashboardAuthorizationContributor : AuthorizationContributor {

    companion object {
        const val DASHBOARD = "dashboard"
    }

    override fun getAuthorizations(user: OntrackAuthenticatedUser): List<Authorization> =
        listOf(
            Authorization(DASHBOARD, Authorization.EDIT, user.isGranted(DashboardEdition::class.java)),
            Authorization(DASHBOARD, Authorization.SHARE, user.isGranted(DashboardSharing::class.java)),
        )
}