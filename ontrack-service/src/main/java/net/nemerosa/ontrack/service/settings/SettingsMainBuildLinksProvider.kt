package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.labels.MainBuildLinksProvider
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

/**
 * Returns the global settings
 */
@Component
class SettingsMainBuildLinksProvider(
        private val settingsService: CachedSettingsService
) : MainBuildLinksProvider {
    override fun getMainBuildLinksConfig(project: Project): MainBuildLinksConfig =
            settingsService.getCachedSettings(MainBuildLinksConfig::class.java)
}
