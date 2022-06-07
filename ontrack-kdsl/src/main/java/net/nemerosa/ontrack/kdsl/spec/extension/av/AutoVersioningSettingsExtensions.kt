package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.spec.settings.SettingsInterface
import net.nemerosa.ontrack.kdsl.spec.settings.SettingsMgt

val SettingsMgt.autoVersioning: SettingsInterface<AutoVersioningSettings>
    get() = SettingsInterface(
        connector = connector,
        id = "auto-versioning",
        type = AutoVersioningSettings::class,
    )
