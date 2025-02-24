package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class MainBuildLinksConfigSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<MainBuildLinksConfig>(
    "main-build-links",
    MainBuildLinksConfig::class,
    settingsManagerService,
    cachedSettingsService,
)
