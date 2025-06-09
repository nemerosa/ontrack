package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.HomePageSettings
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class HomePageSettingsManager(
    cachedSettingsService: CachedSettingsService?,
    securityService: SecurityService?,
    private val settingsRepository: SettingsRepository
) : AbstractSettingsManager<HomePageSettings>(
    HomePageSettings::class.java,
    cachedSettingsService,
    securityService
) {
    override fun doSaveSettings(settings: HomePageSettings) {
        settingsRepository.setInt(
            HomePageSettings::class.java,
            HomePageSettings::maxBranches.name,
            settings.maxBranches
        )
        settingsRepository.setInt(
            HomePageSettings::class.java,
            HomePageSettings::maxProjects.name,
            settings.maxProjects
        )
    }

    override fun getId(): String = "home-page"

    override fun getTitle(): String = "Home page settings"

}