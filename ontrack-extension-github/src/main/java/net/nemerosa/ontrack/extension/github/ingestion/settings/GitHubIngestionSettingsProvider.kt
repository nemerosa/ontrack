package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getPassword
import org.springframework.stereotype.Component

@Component
class GitHubIngestionSettingsProvider(
    private val settingsRepository: SettingsRepository,
    private val encryptionService: EncryptionService,
) : SettingsProvider<GitHubIngestionSettings> {

    override fun getSettings() = GitHubIngestionSettings(
        token = settingsRepository.getPassword(GitHubIngestionSettings::token, "") { encryptionService.decrypt(it) }
    )

    override fun getSettingsClass(): Class<GitHubIngestionSettings> = GitHubIngestionSettings::class.java
}