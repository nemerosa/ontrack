package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getLong
import org.springframework.stereotype.Component

@Component
class WorkflowSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<WorkflowSettings> {

    override fun getSettings() = WorkflowSettings(
        retentionDuration = settingsRepository.getLong(
            WorkflowSettings::retentionDuration,
            WorkflowSettings.DEFAULT_RETENTION_DURATION
        ),
    )

    override fun getSettingsClass(): Class<WorkflowSettings> = WorkflowSettings::class.java
}