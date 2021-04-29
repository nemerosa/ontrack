package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.model.links.BranchLinksSettings
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class BranchLinksSettingsProvider(
    private val settingsRepository: SettingsRepository
) : SettingsProvider<BranchLinksSettings> {

    override fun getSettings() = BranchLinksSettings(
        depth = settingsRepository.getInt(
            BranchLinksSettings::class.java,
            BranchLinksSettings::depth.name,
            BranchLinksSettings.DEFAULT_DEPTH
        ),
        history = settingsRepository.getInt(
            BranchLinksSettings::class.java,
            BranchLinksSettings::history.name,
            BranchLinksSettings.DEFAULT_HISTORY
        ),
        maxLinksPerLevel = settingsRepository.getInt(
            BranchLinksSettings::class.java,
            BranchLinksSettings::maxLinksPerLevel.name,
            BranchLinksSettings.DEFAULT_MAX_LINKS_PER_LEVEL
        )
    )

    override fun getSettingsClass(): Class<BranchLinksSettings> = BranchLinksSettings::class.java
}