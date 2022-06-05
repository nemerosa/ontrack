package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.yesNoField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import org.springframework.stereotype.Component

@Component
class AutoVersioningSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<AutoVersioningSettings>(
    AutoVersioningSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: AutoVersioningSettings) {
        settingsRepository.setBoolean<AutoVersioningSettings>(settings::enabled)
    }

    override fun getSettingsForm(settings: AutoVersioningSettings): Form = Form.create()
        .yesNoField(AutoVersioningSettings::enabled, settings.enabled)

    override fun getId(): String = "auto-versioning"

    override fun getTitle(): String = "Auto Versioning"
}