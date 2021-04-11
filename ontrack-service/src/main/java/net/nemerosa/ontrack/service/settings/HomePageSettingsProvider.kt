package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.settings.DEFAULT_HOME_PAGE_SETTINGS_MAX_BRANCHES
import net.nemerosa.ontrack.model.settings.DEFAULT_HOME_PAGE_SETTINGS_MAX_PROJECTS
import net.nemerosa.ontrack.model.settings.HomePageSettings
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class HomePageSettingsProvider(
    private val settingsRepository: SettingsRepository
) : SettingsProvider<HomePageSettings> {

    override fun getSettings() = HomePageSettings(
        maxBranches = settingsRepository.getInt(
            HomePageSettings::class.java,
            HomePageSettings::maxBranches.name,
            DEFAULT_HOME_PAGE_SETTINGS_MAX_BRANCHES
        ),
        maxProjects = settingsRepository.getInt(
            HomePageSettings::class.java,
            HomePageSettings::maxProjects.name,
            DEFAULT_HOME_PAGE_SETTINGS_MAX_PROJECTS
        )
    )

    override fun getSettingsClass(): Class<HomePageSettings> = HomePageSettings::class.java
}