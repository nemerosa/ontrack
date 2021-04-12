package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.settings.HomePageSettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class HomePageSettingsContext(
    settingsManagerService: SettingsManagerService,
) : AbstractSubSettingsContext<HomePageSettings>(
    "home-page",
    HomePageSettings::class,
    settingsManagerService,
)
