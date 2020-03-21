package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class LabelProviderJobSettingsProvider(
        private val settingsRepository: SettingsRepository,
        private val ontrackConfigProperties: OntrackConfigProperties
) : SettingsProvider<LabelProviderJobSettings> {

    override fun getSettings(): LabelProviderJobSettings =
            LabelProviderJobSettings(
                    settingsRepository.getBoolean(LabelProviderJobSettings::class.java, LabelProviderJobSettings::enabled.name, ontrackConfigProperties.jobLabelProviderEnabled),
                    settingsRepository.getInt(LabelProviderJobSettings::class.java, LabelProviderJobSettings::interval.name, DEFAULT_LABEL_PROVIDER_JOB_INTERVAL),
                    settingsRepository.getBoolean(LabelProviderJobSettings::class.java, LabelProviderJobSettings::perProject.name, DEFAULT_LABEL_PROVIDER_JOB_PER_PROJECT)
            )

    override fun getSettingsClass(): Class<LabelProviderJobSettings> = LabelProviderJobSettings::class.java
}

/**
 * Default interval for scanning labels of projects
 */
internal const val DEFAULT_LABEL_PROVIDER_JOB_INTERVAL = 60

/**
 * Default configuration for the collection of lavels for projects
 */
internal const val DEFAULT_LABEL_PROVIDER_JOB_PER_PROJECT = false
