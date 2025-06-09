package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class SampleDurationSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<SampleDurationSettings>(
    SampleDurationSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: SampleDurationSettings) {
        settingsRepository.setString(
            SampleDurationSettings::class.java,
            SampleDurationSettings::duration.name,
            settings.duration.toString()
        )
    }

    override fun getId(): String = "sample-duration"

    override fun getTitle(): String = "Sample duration"
}