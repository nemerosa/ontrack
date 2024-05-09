package net.nemerosa.ontrack.extension.workflows.acl

import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.RoleContributor
import net.nemerosa.ontrack.model.security.Roles
import org.springframework.stereotype.Component

@Component
class WorkflowRoleContributor : RoleContributor {

    override fun getGlobalFunctionContributionsForGlobalRoles(): Map<String, List<Class<out GlobalFunction>>> =
        mapOf(
            Roles.GLOBAL_ADMINISTRATOR to listOf(
                WorkflowAudit::class.java,
            ),
        )

}