package net.nemerosa.ontrack.extension.casc.context.settings;

import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SampleDurationSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<SampleDurationSettings>(
    "sample-duration",
    SampleDurationSettings::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)
