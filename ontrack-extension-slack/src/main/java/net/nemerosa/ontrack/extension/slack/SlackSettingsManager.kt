package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.model.annotations.getDescription
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
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
    }

    override fun getSettingsForm(settings: SlackSettings): Form = Form.create()
        .with(
            YesNo.of(SlackSettings::enabled.name)
                .label("Enabled")
                .help(getDescription(SlackSettings::enabled))
                .value(settings.enabled)
        )
        .with(
            Password.of(SlackSettings::token.name)
                .label("Token")
                .help(getDescription(SlackSettings::token))
                .value("")
        )

    override fun getId(): String = "slack"

    override fun getTitle(): String = "Slack settings"
}