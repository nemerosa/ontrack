package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class AutoVersioningSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<AutoVersioningSettings>(
    "auto-versioning",
    AutoVersioningSettings::class,
    settingsManagerService,
    cachedSettingsService,
)
