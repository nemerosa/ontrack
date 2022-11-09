package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyLabel
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setInt
import net.nemerosa.ontrack.model.support.setString
import org.springframework.stereotype.Component

@Component
class GitHubIngestionSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    val settingsRepository: SettingsRepository,
    val encryptionService: EncryptionService,
) : AbstractSettingsManager<GitHubIngestionSettings>(
    GitHubIngestionSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: GitHubIngestionSettings) {
        settingsRepository.setPassword(
            GitHubIngestionSettings::class.java,
            GitHubIngestionSettings::token.name,
            settings.token,
            true,
        ) { encryptionService.encrypt(it) }
        settingsRepository.setInt(
            GitHubIngestionSettings::class.java,
            GitHubIngestionSettings::retentionDays.name,
            settings.retentionDays
        )
        settingsRepository.setBoolean<GitHubIngestionSettings>(settings::orgProjectPrefix)
        settingsRepository.setInt<GitHubIngestionSettings>(settings::indexationInterval)
        settingsRepository.setString<GitHubIngestionSettings>(settings::repositoryIncludes)
        settingsRepository.setString<GitHubIngestionSettings>(settings::repositoryExcludes)
        settingsRepository.setString<GitHubIngestionSettings>(settings::issueServiceIdentifier)
        settingsRepository.setBoolean<GitHubIngestionSettings>(settings::enabled)
    }

    override fun getSettingsForm(settings: GitHubIngestionSettings?): Form =
        Form.create()
            .with(
                YesNo.of(GitHubIngestionSettings::enabled.name)
                    .label(getPropertyLabel(GitHubIngestionSettings::enabled))
                    .help(getPropertyDescription(GitHubIngestionSettings::enabled))
                    .value(settings?.enabled ?: GitHubIngestionSettings.DEFAULT_ENABLED)
            )
            .with(
                Password.of(GitHubIngestionSettings::token.name)
                    .label("Token")
                    .help("Secret to set in the hook configuration")
                    .optional()
            )
            .with(
                Int.of(GitHubIngestionSettings::retentionDays.name)
                    .label("Retention days")
                    .help("Number of days to keep the received payloads (0 = forever)")
                    .min(0)
                    .value(settings?.retentionDays ?: GitHubIngestionSettings.DEFAULT_RETENTION_DAYS)
            )
            .with(
                YesNo.of(GitHubIngestionSettings::orgProjectPrefix.name)
                    .label("Org project prefix")
                    .help("Must the organization name be used as a project name prefix?")
                    .value(settings?.orgProjectPrefix ?: GitHubIngestionSettings.DEFAULT_ORG_PROJECT_PREFIX)
            )
            .with(
                Int.of(GitHubIngestionSettings::indexationInterval.name)
                    .label("Indexation interval")
                    .help("Default indexation interval (in minutes) when configuring the GitHub projects")
                    .min(0)
                    .value(settings?.indexationInterval ?: GitHubIngestionSettings.DEFAULT_INDEXATION_INTERVAL)
            )
            .with(
                Text.of(GitHubIngestionSettings::repositoryIncludes.name)
                    .label(getPropertyLabel(GitHubIngestionSettings::repositoryIncludes))
                    .help(getPropertyDescription(GitHubIngestionSettings::repositoryIncludes))
                    .value(settings?.repositoryIncludes ?: GitHubIngestionSettings.DEFAULT_REPOSITORY_INCLUDES)
            )
            .with(
                Text.of(GitHubIngestionSettings::repositoryExcludes.name)
                    .label(getPropertyLabel(GitHubIngestionSettings::repositoryExcludes))
                    .help(getPropertyDescription(GitHubIngestionSettings::repositoryExcludes))
                    .optional()
                    .value(settings?.repositoryExcludes ?: GitHubIngestionSettings.DEFAULT_REPOSITORY_EXCLUDES)
            )
            .with(
                Text.of(GitHubIngestionSettings::issueServiceIdentifier.name)
                    .label(getPropertyLabel(GitHubIngestionSettings::issueServiceIdentifier))
                    .help(getPropertyDescription(GitHubIngestionSettings::issueServiceIdentifier))
                    .value(settings?.issueServiceIdentifier ?: GitHubIngestionSettings.DEFAULT_ISSUE_SERVICE_IDENTIFIER)
            )

    override fun getId(): String = "github-ingestion"

    override fun getTitle(): String = "GitHub workflow ingestion"
}