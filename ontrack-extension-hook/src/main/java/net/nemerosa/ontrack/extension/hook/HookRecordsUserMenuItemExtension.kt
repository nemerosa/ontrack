package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class HookRecordsUserMenuItemExtension(
    extensionFeature: HookExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = if (securityService.isGlobalFunctionGranted<ApplicationManagement>()) {
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.SYSTEM,
                    extension = "extension/${feature.id}",
                    id = "hook-records",
                    name = "Hook records",
                )
            )
        } else {
            emptyList()
        }
}