package net.nemerosa.ontrack.extension.casc.support

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SampleSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<SampleSettings>(
    field = "sample",
    settingsClass = SampleSettings::class,
    settingsManagerService = settingsManagerService,
    cachedSettingsService = cachedSettingsService,
)
