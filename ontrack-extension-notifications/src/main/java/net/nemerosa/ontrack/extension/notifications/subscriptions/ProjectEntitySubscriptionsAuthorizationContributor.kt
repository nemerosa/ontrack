package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.subscriptions.GlobalSubscriptionsAuthorizationContributor.Companion.SUBSCRIPTIONS
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class ProjectEntitySubscriptionsAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    /**
     * All project entities but the builds, validation runs & promotion runs.
     */
    override fun appliesTo(context: Any): Boolean = context is ProjectEntity &&
            !(
                    context.projectEntityType == ProjectEntityType.BUILD ||
                            context.projectEntityType == ProjectEntityType.VALIDATION_RUN ||
                            context.projectEntityType == ProjectEntityType.PROMOTION_RUN
                    )

    override fun getAuthorizations(user: AuthenticatedUser, context: Any): List<Authorization> {
        val entity = context as ProjectEntity
        return listOf(
            Authorization(
                SUBSCRIPTIONS,
                Authorization.VIEW,
                securityService.isProjectFunctionGranted<ProjectSubscriptionsRead>(entity)
            ),
            Authorization(
                SUBSCRIPTIONS,
                Authorization.EDIT,
                securityService.isProjectFunctionGranted<ProjectSubscriptionsWrite>(entity)
            ),
        )
    }
}