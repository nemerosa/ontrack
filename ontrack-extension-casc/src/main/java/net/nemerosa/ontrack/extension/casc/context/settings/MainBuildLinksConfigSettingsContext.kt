package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class MainBuildLinksConfigSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<MainBuildLinksConfig>(
    "main-build-links",
    MainBuildLinksConfig::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)
