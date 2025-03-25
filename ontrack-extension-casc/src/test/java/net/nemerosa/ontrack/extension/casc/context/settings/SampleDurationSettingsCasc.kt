package net.nemerosa.ontrack.extension.casc.context.settings;

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SampleDurationSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<SampleDurationSettings>(
    "sample-duration",
    SampleDurationSettings::class,
    settingsManagerService,
    cachedSettingsService,
)
