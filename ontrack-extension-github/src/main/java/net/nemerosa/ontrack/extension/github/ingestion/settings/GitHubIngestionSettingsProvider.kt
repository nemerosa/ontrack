package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.*
import org.springframework.stereotype.Component

@Component
class GitHubIngestionSettingsProvider(
    private val settingsRepository: SettingsRepository,
    private val encryptionService: EncryptionService,
) : SettingsProvider<GitHubIngestionSettings> {

    override fun getSettings() = GitHubIngestionSettings(
        token = settingsRepository.getPassword(GitHubIngestionSettings::token, "") { encryptionService.decrypt(it) },
        retentionDays = settingsRepository.getInt(
            GitHubIngestionSettings::retentionDays,
            GitHubIngestionSettings.DEFAULT_RETENTION_DAYS
        ),
        orgProjectPrefix = settingsRepository.getBoolean(
            GitHubIngestionSettings::orgProjectPrefix,
            GitHubIngestionSettings.DEFAULT_ORG_PROJECT_PREFIX
        ),
        indexationInterval = settingsRepository.getInt(
            GitHubIngestionSettings::indexationInterval,
            GitHubIngestionSettings.DEFAULT_INDEXATION_INTERVAL,
        ),
        repositoryIncludes = settingsRepository.getString(
            GitHubIngestionSettings::repositoryIncludes,
            GitHubIngestionSettings.DEFAULT_REPOSITORY_INCLUDES,
        ),
        repositoryExcludes = settingsRepository.getString(
            GitHubIngestionSettings::repositoryExcludes,
            GitHubIngestionSettings.DEFAULT_REPOSITORY_EXCLUDES,
        ),
        issueServiceIdentifier = settingsRepository.getString(
            GitHubIngestionSettings::issueServiceIdentifier,
            GitHubIngestionSettings.DEFAULT_ISSUE_SERVICE_IDENTIFIER,
        ),
        enabled = settingsRepository.getBoolean(
            GitHubIngestionSettings::enabled,
            GitHubIngestionSettings.DEFAULT_ENABLED,
        ),
    )

    override fun getSettingsClass(): Class<GitHubIngestionSettings> = GitHubIngestionSettings::class.java
}