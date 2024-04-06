package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component

@Component
class ProjectEntitySubscriptionsAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    override fun appliesTo(context: Any): Boolean = context is ProjectEntity

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> {
        val entity = context as ProjectEntity
        return listOf(
            Authorization(
                "subscriptions",
                Authorization.VIEW,
                securityService.isProjectFunctionGranted<ProjectSubscriptionsRead>(entity)
            ),
            Authorization(
                "subscriptions",
                Authorization.EDIT,
                securityService.isProjectFunctionGranted<ProjectSubscriptionsWrite>(entity)
            ),
        )
    }
}