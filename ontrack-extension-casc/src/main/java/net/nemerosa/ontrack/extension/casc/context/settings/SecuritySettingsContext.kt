package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SecuritySettingsContext(
    settingsManagerService: SettingsManagerService,
) : AbstractSubSettingsContext<SecuritySettings>(
    "security",
    SecuritySettings::class,
    settingsManagerService,
)