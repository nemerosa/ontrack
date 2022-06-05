package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import org.springframework.stereotype.Component

@Component
class AutoVersioningSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<AutoVersioningSettings> {

    override fun getSettings() = AutoVersioningSettings(
        enabled = settingsRepository.getBoolean(
            AutoVersioningSettings::enabled,
            AutoVersioningSettings.DEFAULT_ENABLED
        ),
    )

    override fun getSettingsClass(): Class<AutoVersioningSettings> = AutoVersioningSettings::class.java
}