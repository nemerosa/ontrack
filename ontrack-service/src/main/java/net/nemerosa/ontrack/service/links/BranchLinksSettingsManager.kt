package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.model.links.BranchLinksSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class BranchLinksSettingsManager(
    cachedSettingsService: CachedSettingsService?,
    securityService: SecurityService?,
    private val settingsRepository: SettingsRepository
) : AbstractSettingsManager<BranchLinksSettings>(
    BranchLinksSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: BranchLinksSettings) {
        settingsRepository.setInt(BranchLinksSettings::class.java, BranchLinksSettings::depth.name, settings.depth)
        settingsRepository.setInt(BranchLinksSettings::class.java, BranchLinksSettings::history.name, settings.history)
        settingsRepository.setInt(BranchLinksSettings::class.java, BranchLinksSettings::maxLinksPerLevel.name, settings.maxLinksPerLevel)
    }

    override fun getId(): String = "branch-links"

    override fun getTitle(): String = "Branch graph"
}