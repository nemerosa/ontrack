package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.JobHistorySettings
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class JobHistorySettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<JobHistorySettings>(
    JobHistorySettings::class.java,
    cachedSettingsService,
    securityService
) {
    override fun doSaveSettings(settings: JobHistorySettings) {
        settingsRepository.setString(
            JobHistorySettings::class.java,
            JobHistorySettings::retention.name,
            settings.retention.toString()
        )
    }

    override fun getId(): String = "job-history"

    override fun getTitle(): String = "Job history settings"
}