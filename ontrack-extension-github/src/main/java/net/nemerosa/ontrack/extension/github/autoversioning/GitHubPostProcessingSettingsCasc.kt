package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class GitHubPostProcessingSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<GitHubPostProcessingSettings>(
    "github-av-post-processing",
    GitHubPostProcessingSettings::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)
