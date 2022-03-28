package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class SubscriptionsRoleContributor : RoleContributor {

    companion object {
        const val GLOBAL_SUBSCRIPTIONS_MANAGER = "GLOBAL_SUBSCRIPTIONS_MANAGER"
    }

    override fun getGlobalRoles() = listOf(
        RoleDefinition(
            id = GLOBAL_SUBSCRIPTIONS_MANAGER,
            name = "Global subscriptions manager",
            description = "Right to manage subscriptions at global level",
        )
    )

    override fun getGlobalFunctionContributionsForGlobalRoles(): Map<String, List<Class<out GlobalFunction>>> = mapOf(
        GLOBAL_SUBSCRIPTIONS_MANAGER to listOf(
            GlobalSubscriptionsManage::class.java
        )
    )

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