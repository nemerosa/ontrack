package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setString
import org.springframework.stereotype.Component

@Component
class SlackSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
    private val encryptionService: EncryptionService,
) : AbstractSettingsManager<SlackSettings>(
    SlackSettings::class.java,
    cachedSettingsService,
    securityService,
) {
    override fun doSaveSettings(settings: SlackSettings) {
        settingsRepository.setBoolean<SlackSettings>(settings::enabled)
        settingsRepository.setPassword(
            SlackSettings::class.java,
            SlackSettings::token.name,
            settings.token,
            true
        ) { encryptionService.encrypt(it) }
        settingsRepository.setString<SlackSettings>(settings::emoji)
        settingsRepository.setString<SlackSettings>(settings::endpoint)
    }

    override fun getId(): String = "slack"

    override fun getTitle(): String = "Slack settings"
}