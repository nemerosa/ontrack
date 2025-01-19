package net.nemerosa.ontrack.extension.queue.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class QueueUserMenuItemExtension(
    extension: QueueExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extension), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = if (securityService.isGlobalFunctionGranted(ApplicationManagement::class.java)) {
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.SYSTEM,
                    extension = feature,
                    id = "records",
                    name = "Queue records",
                )

            )
        } else {
            emptyList()
        }

}