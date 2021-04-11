package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.*
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

    override fun getSettingsForm(settings: HomePageSettings?): Form =
        Form.create()
            .with(
                Int.of(HomePageSettings::maxBranches.name)
                    .label("Max branches")
                    .help("Maximum of branches to display per favorite project")
                    .min(1)
                    .value(settings?.maxBranches ?: DEFAULT_HOME_PAGE_SETTINGS_MAX_BRANCHES)
            )
            .with(
                Int.of(HomePageSettings::maxProjects.name)
                    .label("Max projects")
                    .help("Maximum of projects starting from which we need to switch to a search mode")
                    .min(1)
                    .value(settings?.maxProjects ?: DEFAULT_HOME_PAGE_SETTINGS_MAX_PROJECTS)
            )

    override fun getId(): String = "home-page"

    override fun getTitle(): String = "Home page settings"

}