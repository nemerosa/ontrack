package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class WebhooksUserMenuItemExtension(
    private val notificationsExtensionFeature: NotificationsExtensionFeature,
    private val securityService: SecurityService,
    private val cachedSettingsService: CachedSettingsService,
) : AbstractExtension(notificationsExtensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (cachedSettingsService.getCachedSettings(WebhookSettings::class.java).enabled && securityService.isGlobalFunctionGranted<WebhookManagement>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.SYSTEM,
                    extension = "extension/${notificationsExtensionFeature.id}",
                    id = "webhooks",
                    name = "Webhooks",
                )
            } else {
                null
            }
        )

}