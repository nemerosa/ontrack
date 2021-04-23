package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.LabelProviderJobSettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class LabelProviderJobSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<LabelProviderJobSettings>(
    "label-provider-job",
    LabelProviderJobSettings::class,
    settingsManagerService,
    cachedSettingsService
)
