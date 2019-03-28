package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

/**
 * List of project labels identifying the build links to keep in decorations.
 */
@Component
class MainBuildLinksSettingsProvider(
        private val settingsRepository: SettingsRepository
) : SettingsProvider<MainBuildLinksConfig> {

    override fun getSettings(): MainBuildLinksConfig {
        return MainBuildLinksConfig(
                settingsRepository
                        .getString(MainBuildLinksConfig::class.java, "labels", "")
                        .split("|")
        )
    }

    override fun getSettingsClass(): Class<MainBuildLinksConfig> = MainBuildLinksConfig::class.java
}