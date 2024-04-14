package net.nemerosa.ontrack.kdsl.spec.extension.slack

import net.nemerosa.ontrack.kdsl.spec.settings.SettingsInterface
import net.nemerosa.ontrack.kdsl.spec.settings.SettingsMgt

val SettingsMgt.slack: SettingsInterface<SlackSettings>
    get() = SettingsInterface(
        connector = connector,
        id = "slack",
        type = SlackSettings::class,
    )
