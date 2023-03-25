package net.nemerosa.ontrack.extension.tfc.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import org.springframework.stereotype.Component

@Component
class TFCSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<TFCSettings> {

    override fun getSettings() = TFCSettings(
        enabled = settingsRepository.getBoolean(TFCSettings::enabled, false),
    )

    override fun getSettingsClass(): Class<TFCSettings> = TFCSettings::class.java
}