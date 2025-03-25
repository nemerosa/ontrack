package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.common.parseDuration
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class SampleDurationSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<SampleDurationSettings> {

    override fun getSettings() = SampleDurationSettings(
        duration = settingsRepository.getString(
            SampleDurationSettings::class.java,
            SampleDurationSettings::duration.name,
            ""
        )
            ?.takeIf { it.isNotBlank() }
            ?.let { parseDuration(it) }
            ?: Duration.ofSeconds(10)
    )

    override fun getSettingsClass(): Class<SampleDurationSettings> = SampleDurationSettings::class.java
}