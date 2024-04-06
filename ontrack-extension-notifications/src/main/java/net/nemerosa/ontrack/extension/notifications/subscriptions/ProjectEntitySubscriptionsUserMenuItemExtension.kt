package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.api.ProjectEntityUserMenuItemExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class ProjectEntitySubscriptionsUserMenuItemExtension(
    extensionFeature: NotificationsExtensionFeature,
    private val securityService: SecurityService,
    private val notificationsConfigProperties: NotificationsConfigProperties,
) : AbstractExtension(extensionFeature), ProjectEntityUserMenuItemExtension {
    override fun getItems(projectEntity: ProjectEntity): List<UserMenuItem> =
        if (
            securityService.isProjectFunctionGranted(projectEntity, ProjectSubscriptionsRead::class.java) &&
            notificationsConfigProperties.enabled
        ) {
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.INFORMATION,
                    extension = feature,
                    id = "subscriptions/entity/${projectEntity.projectEntityType}/${projectEntity.id}",
                    name = "Subscriptions",
                ),
            )
        } else {
            emptyList()
        }
}