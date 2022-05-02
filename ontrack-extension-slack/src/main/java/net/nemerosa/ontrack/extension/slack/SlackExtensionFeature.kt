package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class SlackExtensionFeature(
    private val cascExtensionFeature: CascExtensionFeature,
    private val notificationsExtensionFeature: NotificationsExtensionFeature,
) : AbstractExtensionFeature(
    "slack",
    "Slack",
    "Support for Slack notifications",
    ExtensionFeatureOptions.DEFAULT
        .withDependency(cascExtensionFeature)
        .withDependency(notificationsExtensionFeature)
        .withGui(true)
)