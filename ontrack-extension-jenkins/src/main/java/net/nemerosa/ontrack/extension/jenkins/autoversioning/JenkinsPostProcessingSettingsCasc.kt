package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class JenkinsPostProcessingSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<JenkinsPostProcessingSettings>(
    "auto-versioning-jenkins",
    JenkinsPostProcessingSettings::class,
    settingsManagerService,
    cachedSettingsService
) {
}