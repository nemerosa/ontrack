package net.nemerosa.ontrack.kdsl.spec.settings

val SettingsMgt.security: SettingsInterface<SecuritySettings>
    get() = SettingsInterface(
        connector = connector,
        id = "general-security",
        type = SecuritySettings::class,
    )
