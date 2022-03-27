package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.RoleContributor
import net.nemerosa.ontrack.model.security.Roles
import org.springframework.stereotype.Component

@Component
class SubscriptionsRoleContributor : RoleContributor {

    override fun getProjectFunctionContributionsForGlobalRoles(): Map<String, List<Class<out ProjectFunction>>> =
        mapOf(
            Roles.GLOBAL_PARTICIPANT to listOf(
                ProjectSubscriptionsRead::class.java
            )
        )

    override fun getProjectFunctionContributionsForProjectRoles(): Map<String, List<Class<out ProjectFunction>>> =
        mapOf(
            Roles.PROJECT_PARTICIPANT to listOf(
                ProjectSubscriptionsRead::class.java,
            ),
            Roles.PROJECT_MANAGER to listOf(
                ProjectSubscriptionsRead::class.java,
                ProjectSubscriptionsWrite::class.java,
            ),
            Roles.PROJECT_OWNER to listOf(
                ProjectSubscriptionsRead::class.java,
                ProjectSubscriptionsWrite::class.java,
            ),
        )
}