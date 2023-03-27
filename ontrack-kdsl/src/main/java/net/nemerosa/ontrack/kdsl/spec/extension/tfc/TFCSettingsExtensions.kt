package net.nemerosa.ontrack.kdsl.spec.extension.tfc

import net.nemerosa.ontrack.kdsl.spec.settings.SettingsInterface
import net.nemerosa.ontrack.kdsl.spec.settings.SettingsMgt

val SettingsMgt.tfc: SettingsInterface<TFCSettings>
    get() = SettingsInterface(
        connector = connector,
        id = "tfc",
        type = TFCSettings::class,
    )
