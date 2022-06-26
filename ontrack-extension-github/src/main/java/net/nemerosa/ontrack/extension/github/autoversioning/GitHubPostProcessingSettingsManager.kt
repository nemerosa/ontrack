package net.nemerosa.ontrack.extension.github.autoversioning

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
class GitHubPostProcessingSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<GitHubPostProcessingSettings>(
    GitHubPostProcessingSettings::class.java,
    cachedSettingsService,
    securityService
) {
    override fun doSaveSettings(settings: GitHubPostProcessingSettings) {
        settingsRepository.setString<GitHubPostProcessingSettings>(settings::config)
        settingsRepository.setString<GitHubPostProcessingSettings>(settings::repository)
        settingsRepository.setString<GitHubPostProcessingSettings>(settings::workflow)
        settingsRepository.setString<GitHubPostProcessingSettings>(settings::branch)
        settingsRepository.setInt<GitHubPostProcessingSettings>(settings::retries)
        settingsRepository.setInt<GitHubPostProcessingSettings>(settings::retriesDelaySeconds)
    }

    override fun getSettingsForm(settings: GitHubPostProcessingSettings): Form = Form.create()
        .textField(GitHubPostProcessingSettings::config, settings.config)
        .textField(GitHubPostProcessingSettings::repository, settings.repository)
        .textField(GitHubPostProcessingSettings::workflow, settings.workflow)
        .textField(GitHubPostProcessingSettings::branch, settings.branch)
        .intField(GitHubPostProcessingSettings::retries, settings.retries)
        .intField(GitHubPostProcessingSettings::retriesDelaySeconds, settings.retriesDelaySeconds)

    override fun getId(): String = "github-av-post-processing"

    override fun getTitle(): String = "GitHub Auto Versioning Post Processing"

}