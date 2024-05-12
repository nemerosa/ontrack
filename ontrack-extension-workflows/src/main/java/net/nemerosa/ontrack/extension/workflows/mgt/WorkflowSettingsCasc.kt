package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascField
import net.nemerosa.ontrack.extension.casc.schema.cascObject
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
    cachedSettingsService
) {

    override val type: CascType = cascObject(
        "Global settings for the management of workflows",
        cascField(WorkflowSettings::retentionDuration),
    )
}