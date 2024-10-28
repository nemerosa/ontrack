package net.nemerosa.ontrack.extension.license.settings

import net.nemerosa.ontrack.extension.license.signature.AbstractSignatureLicenseService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service


@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "settings",
    matchIfMissing = false
)
class SettingsLicenseService(
    private val cachedSettingsService: CachedSettingsService,
) : AbstractSignatureLicenseService() {

    override val licenseType: String = "Settings"

    override val encodedLicense: String?
        get() = cachedSettingsService.getCachedSettings(LicenseSettings::class.java).key

}