package net.nemerosa.ontrack.extension.casc.support

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setInt
import org.springframework.stereotype.Component

@Component
class SampleSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<SampleSettings>(SampleSettings::class.java, cachedSettingsService, securityService) {

    override fun doSaveSettings(settings: SampleSettings) {
        settingsRepository.setInt<SampleSettings>(settings::maxProjects)
        settingsRepository.setBoolean<SampleSettings>(settings::enabled)
    }

    override fun getId(): String = "sample"

    override fun getTitle(): String = "Sample"
}