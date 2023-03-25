package net.nemerosa.ontrack.extension.tfc.settings

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.yesNoField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import org.springframework.stereotype.Component

@Component
class TFCSettingsManager(
    private val settingsRepository: SettingsRepository,
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
) : AbstractSettingsManager<TFCSettings>(
    TFCSettings::class.java,
    cachedSettingsService,
    securityService
) {
    override fun doSaveSettings(settings: TFCSettings) {
        settingsRepository.setBoolean<TFCSettings>(settings::enabled)
    }

    override fun getSettingsForm(settings: TFCSettings?): Form = Form.create()
        .yesNoField(TFCSettings::enabled, settings?.enabled ?: false)

    override fun getId(): String = "tfc"

    override fun getTitle(): String = "Terraform Cloud"
}