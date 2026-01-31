package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.JobHistorySettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class JobHistorySettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService
) : AbstractSubSettingsContext<JobHistorySettings>(
    "job-history",
    JobHistorySettings::class,
    settingsManagerService,
    cachedSettingsService
)