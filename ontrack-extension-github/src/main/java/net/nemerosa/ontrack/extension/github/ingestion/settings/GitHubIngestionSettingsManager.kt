package net.nemerosa.ontrack.extension.github.ingestion.settings

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

    override fun getId(): String = "github-ingestion"

    override fun getTitle(): String = "GitHub ingestion"
}