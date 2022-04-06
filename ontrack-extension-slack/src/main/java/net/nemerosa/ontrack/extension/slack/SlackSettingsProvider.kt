package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import net.nemerosa.ontrack.model.support.getPassword
import net.nemerosa.ontrack.model.support.getString
import org.springframework.stereotype.Component

/**
 * Reading the Slack settings.
 */
@Component
class SlackSettingsProvider(
    private val settingsRepository: SettingsRepository,
    private val encryptionService: EncryptionService,
) : SettingsProvider<SlackSettings> {

    override fun getSettings() = SlackSettings(
        enabled = settingsRepository.getBoolean(SlackSettings::enabled, false),
        token = settingsRepository.getPassword(SlackSettings::token, "", encryptionService::decrypt),
        emoji = settingsRepository.getString(SlackSettings::emoji, ""),
        endpoint = settingsRepository.getString(SlackSettings::endpoint, ""),
    )

    override fun getSettingsClass(): Class<SlackSettings> = SlackSettings::class.java
}