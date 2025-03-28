package net.nemerosa.ontrack.extension.tfc.settings

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class TFCSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<TFCSettings>(
    "tfc",
    TFCSettings::class,
    settingsManagerService,
    cachedSettingsService,
)
