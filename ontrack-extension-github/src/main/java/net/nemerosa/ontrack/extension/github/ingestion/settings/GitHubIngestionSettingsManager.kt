package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
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

    override fun getId(): String = "github-ingestion"

    override fun getTitle(): String = "GitHub workflow ingestion"
}