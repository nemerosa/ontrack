package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.intField
import net.nemerosa.ontrack.model.form.yesNoField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setInt
import org.springframework.stereotype.Component

@Component
class WebhookSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<WebhookSettings>(
    WebhookSettings::class.java,
    cachedSettingsService,
    securityService,
) {
    override fun doSaveSettings(settings: WebhookSettings) {
        settingsRepository.setBoolean<WebhookSettings>(settings::enabled)
        settingsRepository.setInt<WebhookSettings>(settings::timeoutMinutes)
    }

    override fun getSettingsForm(settings: WebhookSettings): Form = Form.create()
        .yesNoField(WebhookSettings::enabled, settings.enabled)
        .intField(WebhookSettings::timeoutMinutes, settings.timeoutMinutes)

    override fun getId(): String = "webhooks"

    override fun getTitle(): String = "Webhook settings"
}
