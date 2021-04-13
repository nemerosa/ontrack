package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SecuritySettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<SecuritySettings>(
    "security",
    SecuritySettings::class,
    settingsManagerService,
    cachedSettingsService,
)