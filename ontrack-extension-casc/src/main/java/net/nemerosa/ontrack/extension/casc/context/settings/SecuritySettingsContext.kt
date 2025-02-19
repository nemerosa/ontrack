package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SecuritySettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<SecuritySettings>(
    "security",
    SecuritySettings::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)