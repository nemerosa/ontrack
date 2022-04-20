package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import net.nemerosa.ontrack.model.support.getInt
import org.springframework.stereotype.Component

/**
 * Reading the webhook settings.
 */
@Component
class WebhookSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<WebhookSettings> {

    override fun getSettings() = WebhookSettings(
        enabled = settingsRepository.getBoolean(WebhookSettings::enabled, false),
        timeoutMinutes = settingsRepository.getInt(WebhookSettings::timeoutMinutes, 5),
        deliveriesRetentionDays = settingsRepository.getInt(WebhookSettings::deliveriesRetentionDays, 30),
    )

    override fun getSettingsClass(): Class<WebhookSettings> = WebhookSettings::class.java
}
