package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.longField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setLong
import org.springframework.stereotype.Component

@Component
class WorkflowSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<WorkflowSettings>(
    WorkflowSettings::class.java,
    cachedSettingsService,
    securityService,
) {
    override fun doSaveSettings(settings: WorkflowSettings) {
        settingsRepository.setLong<WorkflowSettings>(settings::retentionDuration)
    }

    override fun getId(): String = "workflows"

    override fun getTitle(): String = "Workflows"

    @Deprecated("Deprecated in Java")
    override fun getSettingsForm(settings: WorkflowSettings?): Form =
        Form.create()
            .longField(
                WorkflowSettings::retentionDuration,
                settings?.retentionDuration
            )
}