package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.getDescription
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
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
    }

    override fun getSettingsForm(settings: WebhookSettings): Form = Form.create()
        .with(
            YesNo.of(WebhookSettings::enabled.name)
                .label("Enabled")
                .help(getDescription(WebhookSettings::enabled))
                .value(settings.enabled)
        )

    override fun getId(): String = "webhooks"

    override fun getTitle(): String = "Webhook settings"
}
