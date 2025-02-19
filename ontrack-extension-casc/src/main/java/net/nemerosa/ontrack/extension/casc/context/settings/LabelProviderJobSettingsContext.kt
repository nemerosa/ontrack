package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.LabelProviderJobSettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class LabelProviderJobSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<LabelProviderJobSettings>(
    "label-provider-job",
    LabelProviderJobSettings::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)
