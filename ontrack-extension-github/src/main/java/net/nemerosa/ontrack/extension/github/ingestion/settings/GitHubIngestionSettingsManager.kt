package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.graphql.support.getName
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
        settingsRepository.setString<GitHubIngestionSettings>(settings::jobIncludes)
        settingsRepository.setString<GitHubIngestionSettings>(settings::jobExcludes)
        settingsRepository.setString<GitHubIngestionSettings>(settings::stepIncludes)
        settingsRepository.setString<GitHubIngestionSettings>(settings::stepExcludes)
    }

    override fun getSettingsForm(settings: GitHubIngestionSettings?): Form =
        Form.create()
            .with(
                Password.of(GitHubIngestionSettings::token.name)
                    .label("Token")
                    .help("Secret to set in the hook configuration")
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
                    .label(getName(GitHubIngestionSettings::repositoryIncludes))
                    .help(getDescription(GitHubIngestionSettings::repositoryIncludes))
                    .value(settings?.repositoryIncludes ?: GitHubIngestionSettings.DEFAULT_REPOSITORY_INCLUDES)
            )
            .with(
                Text.of(GitHubIngestionSettings::repositoryExcludes.name)
                    .label(getName(GitHubIngestionSettings::repositoryExcludes))
                    .help(getDescription(GitHubIngestionSettings::repositoryExcludes))
                    .value(settings?.repositoryExcludes ?: GitHubIngestionSettings.DEFAULT_REPOSITORY_EXCLUDES)
            )
            .with(
                Text.of(GitHubIngestionSettings::jobIncludes.name)
                    .label(getName(GitHubIngestionSettings::jobIncludes))
                    .help(getDescription(GitHubIngestionSettings::jobIncludes))
                    .value(settings?.jobIncludes ?: GitHubIngestionSettings.DEFAULT_JOB_INCLUDES)
            )
            .with(
                Text.of(GitHubIngestionSettings::jobExcludes.name)
                    .label(getName(GitHubIngestionSettings::jobExcludes))
                    .help(getDescription(GitHubIngestionSettings::jobExcludes))
                    .value(settings?.jobExcludes ?: GitHubIngestionSettings.DEFAULT_JOB_EXCLUDES)
            )
            .with(
                Text.of(GitHubIngestionSettings::stepIncludes.name)
                    .label(getName(GitHubIngestionSettings::stepIncludes))
                    .help(getDescription(GitHubIngestionSettings::stepIncludes))
                    .value(settings?.stepIncludes ?: GitHubIngestionSettings.DEFAULT_STEP_INCLUDES)
            )
            .with(
                Text.of(GitHubIngestionSettings::stepExcludes.name)
                    .label(getName(GitHubIngestionSettings::stepExcludes))
                    .help(getDescription(GitHubIngestionSettings::stepExcludes))
                    .value(settings?.stepExcludes ?: GitHubIngestionSettings.DEFAULT_STEP_EXCLUDES)
            )

    override fun getId(): String = "github-ingestion"

    override fun getTitle(): String = "GitHub workflow ingestion"
}