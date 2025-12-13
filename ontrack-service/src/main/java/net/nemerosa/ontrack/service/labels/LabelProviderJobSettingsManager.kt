package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.LabelProviderJobSettings
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class LabelProviderJobSettingsManager(
    private val settingsRepository: SettingsRepository,
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService
) : AbstractSettingsManager<LabelProviderJobSettings>(
    LabelProviderJobSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: LabelProviderJobSettings) {
        settingsRepository.setBoolean(
            LabelProviderJobSettings::class.java,
            LabelProviderJobSettings::enabled.name,
            settings.enabled
        )
        settingsRepository.setInt(
            LabelProviderJobSettings::class.java,
            LabelProviderJobSettings::interval.name,
            settings.interval
        )
        settingsRepository.setBoolean(
            LabelProviderJobSettings::class.java,
            LabelProviderJobSettings::perProject.name,
            settings.perProject
        )
    }

    override fun getId(): String = "label-provider-job"

    override fun getTitle(): String = "Label provider job"

}