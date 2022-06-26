package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getInt
import net.nemerosa.ontrack.model.support.getString
import org.springframework.stereotype.Component

@Component
class GitHubPostProcessingSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<GitHubPostProcessingSettings> {

    override fun getSettings() = GitHubPostProcessingSettings(
        config = settingsRepository.getString(GitHubPostProcessingSettings::config, ""),
        repository = settingsRepository.getString(GitHubPostProcessingSettings::repository, ""),
        workflow = settingsRepository.getString(GitHubPostProcessingSettings::workflow, ""),
        branch = settingsRepository.getString(
            GitHubPostProcessingSettings::branch,
            GitHubPostProcessingSettings.DEFAULT_BRANCH
        ),
        retries = settingsRepository.getInt(
            GitHubPostProcessingSettings::retries,
            GitHubPostProcessingSettings.DEFAULT_RETRIES
        ),
        retriesDelaySeconds = settingsRepository.getInt(
            GitHubPostProcessingSettings::retriesDelaySeconds,
            GitHubPostProcessingSettings.DEFAULT_RETRIES_DELAY_SECONDS
        ),
    )

    override fun getSettingsClass(): Class<GitHubPostProcessingSettings> = GitHubPostProcessingSettings::class.java
}