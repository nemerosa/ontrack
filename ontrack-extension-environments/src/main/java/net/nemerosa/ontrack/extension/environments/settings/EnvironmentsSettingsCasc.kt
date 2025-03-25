package net.nemerosa.ontrack.extension.environments.settings

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class EnvironmentsSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<EnvironmentsSettings>(
    "environments",
    EnvironmentsSettings::class,
    settingsManagerService,
    cachedSettingsService,
)
