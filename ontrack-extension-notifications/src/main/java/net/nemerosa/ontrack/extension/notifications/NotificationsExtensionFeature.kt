package net.nemerosa.ontrack.extension.notifications

import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class NotificationsExtensionFeature(
    private val cascExtensionFeature: CascExtensionFeature,
) : AbstractExtensionFeature(
    "notifications",
    "Notifications",
    "Support for notifications and webhooks",
    ExtensionFeatureOptions.DEFAULT
        .withDependency(cascExtensionFeature)
        .withGui(true)
)