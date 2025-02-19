package net.nemerosa.ontrack.extension.hook.settings

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class HookSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<HookSettings>(
    "hook",
    HookSettings::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)
