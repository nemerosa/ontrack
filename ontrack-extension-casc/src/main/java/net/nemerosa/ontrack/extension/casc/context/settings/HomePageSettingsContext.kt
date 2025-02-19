package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.HomePageSettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class HomePageSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<HomePageSettings>(
    "home-page",
    HomePageSettings::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)
