package net.nemerosa.ontrack.extension.casc.support

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import net.nemerosa.ontrack.model.support.getInt
import org.springframework.stereotype.Component

@Component
class SampleSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<SampleSettings> {

    override fun getSettings(): SampleSettings = SampleSettings(
        maxProjects = settingsRepository.getInt(SampleSettings::maxProjects, 0),
        enabled = settingsRepository.getBoolean(SampleSettings::enabled, false),
    )

    override fun getSettingsClass(): Class<SampleSettings> = SampleSettings::class.java
}