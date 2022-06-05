package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.intField
import net.nemerosa.ontrack.model.form.textField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setInt
import net.nemerosa.ontrack.model.support.setString
import org.springframework.stereotype.Component

@Component
class JenkinsPostProcessingSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<JenkinsPostProcessingSettings>(
    JenkinsPostProcessingSettings::class.java,
    cachedSettingsService,
    securityService
) {
    override fun doSaveSettings(settings: JenkinsPostProcessingSettings) {
        settingsRepository.setString<JenkinsPostProcessingSettings>(settings::config)
        settingsRepository.setString<JenkinsPostProcessingSettings>(settings::job)
        settingsRepository.setInt<JenkinsPostProcessingSettings>(settings::retries)
        settingsRepository.setInt<JenkinsPostProcessingSettings>(settings::retriesDelaySeconds)
    }

    override fun getId(): String = "jenkins-auto-versioning-processing"

    override fun getTitle(): String = "Jenkins Auto Versioning Processing"

    override fun getSettingsForm(settings: JenkinsPostProcessingSettings): Form =
        Form.create()
            .textField(
                JenkinsPostProcessingSettings::config,
                settings.config
            )
            .textField(
                JenkinsPostProcessingSettings::job,
                settings.job
            )
            .intField(
                JenkinsPostProcessingSettings::retries,
                settings.retries
            )
            .intField(
                JenkinsPostProcessingSettings::retriesDelaySeconds,
                settings.retriesDelaySeconds
            )
}