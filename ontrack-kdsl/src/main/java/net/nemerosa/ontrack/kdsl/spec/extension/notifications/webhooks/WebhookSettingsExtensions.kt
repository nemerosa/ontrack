package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import net.nemerosa.ontrack.kdsl.spec.settings.SettingsInterface
import net.nemerosa.ontrack.kdsl.spec.settings.SettingsMgt

val SettingsMgt.webhooks: SettingsInterface<WebhookSettings>
    get() = SettingsInterface(
        connector = connector,
        id = "webhooks",
        type = WebhookSettings::class,
    )
