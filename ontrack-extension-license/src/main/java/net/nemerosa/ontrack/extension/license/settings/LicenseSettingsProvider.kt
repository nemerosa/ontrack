package net.nemerosa.ontrack.extension.license.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getString
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "settings",
    matchIfMissing = false
)
class LicenseSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<LicenseSettings> {

    override fun getSettings() = LicenseSettings(
        license = settingsRepository.getString(LicenseSettings::license, ""),
    )

    override fun getSettingsClass(): Class<LicenseSettings> = LicenseSettings::class.java
}