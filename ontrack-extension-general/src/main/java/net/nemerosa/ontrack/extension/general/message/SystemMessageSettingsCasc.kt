package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SystemMessageSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<SystemMessageSettings>(
    "system-message",
    SystemMessageSettings::class,
    settingsManagerService,
    cachedSettingsService,
)
