package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getInt
import net.nemerosa.ontrack.model.support.getString
import org.springframework.stereotype.Component

@Component
class JenkinsPostProcessingSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<JenkinsPostProcessingSettings> {

    override fun getSettings() = JenkinsPostProcessingSettings(
        config = settingsRepository.getString(
            JenkinsPostProcessingSettings::config,
            ""
        ),
        job = settingsRepository.getString(
            JenkinsPostProcessingSettings::job,
            ""
        ),
        retries = settingsRepository.getInt(
            JenkinsPostProcessingSettings::retries,
            JenkinsPostProcessingSettings.DEFAULT_RETRIES
        ),
        retriesDelaySeconds = settingsRepository.getInt(
            JenkinsPostProcessingSettings::retriesDelaySeconds,
            JenkinsPostProcessingSettings.DEFAULT_RETRIES_DELAY_SECONDS
        )
    )

    override fun getSettingsClass(): Class<JenkinsPostProcessingSettings> = JenkinsPostProcessingSettings::class.java

}