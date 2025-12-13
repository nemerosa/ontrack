package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.RoleContributor
import net.nemerosa.ontrack.model.security.Roles
import org.springframework.stereotype.Component

@Component
class WebhookRoleContributor : RoleContributor {

    override fun getGlobalFunctionContributionsForGlobalRoles(): Map<String, List<Class<out GlobalFunction>>> = mapOf(
        Roles.GLOBAL_ADMINISTRATOR to listOf(
            WebhookManagement::class.java
        )
    )

}
