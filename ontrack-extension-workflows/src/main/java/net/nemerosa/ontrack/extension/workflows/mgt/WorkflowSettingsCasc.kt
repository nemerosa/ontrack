package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class WorkflowSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<WorkflowSettings>(
    "workflows",
    WorkflowSettings::class,
    settingsManagerService,
    cachedSettingsService,
)