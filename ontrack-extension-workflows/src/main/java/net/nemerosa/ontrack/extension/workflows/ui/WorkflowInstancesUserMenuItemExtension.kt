package net.nemerosa.ontrack.extension.workflows.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.acl.WorkflowAudit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class WorkflowInstancesUserMenuItemExtension(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(workflowsExtensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() {
            val list = mutableListOf<UserMenuItem>()

            if (securityService.isGlobalFunctionGranted<WorkflowAudit>()) {
                list += UserMenuItem(
                    groupId = CoreUserMenuGroups.INFORMATION,
                    extension = feature,
                    id = "audit",
                    name = "Workflows audit",
                )
            }

            return list.toList()
        }
}