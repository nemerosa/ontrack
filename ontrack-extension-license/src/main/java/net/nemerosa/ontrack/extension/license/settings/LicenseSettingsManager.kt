package net.nemerosa.ontrack.extension.license.settings

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.memoField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setString
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "settings",
    matchIfMissing = false
)
@Component
class LicenseSettingsManager(
    cachedSettingsService: CachedSettingsService?,
    securityService: SecurityService?,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<LicenseSettings>(
    LicenseSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: LicenseSettings) {
        settingsRepository.setString<LicenseSettings>(settings::license)
    }

    override fun getId(): String = "license"

    override fun getTitle(): String = "License"

    @Deprecated("Deprecated in Java")
    override fun getSettingsForm(settings: LicenseSettings): Form =
        Form.create()
            .memoField(LicenseSettings::license, settings.license)

}