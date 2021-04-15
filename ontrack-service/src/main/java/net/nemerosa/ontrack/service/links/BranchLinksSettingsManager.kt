package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
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

    override fun getSettingsForm(settings: BranchLinksSettings?): Form = Form.create()
        .with(
            Int.of(BranchLinksSettings::depth.name)
                .label("Dependency depth")
                .help("Dependency depth to take into account when computing the branch links graph")
                .min(1).max(100)
                .value(settings?.depth ?: BranchLinksSettings.DEFAULT_DEPTH)
        )
        .with(
            Int.of(BranchLinksSettings::history.name)
                .label("Build history")
                .help("Build history to take into account when computing the branch links graph")
                .min(1).max(100)
                .value(settings?.history ?: BranchLinksSettings.DEFAULT_HISTORY)
        )
        .with(
            Int.of(BranchLinksSettings::maxLinksPerLevel.name)
                .label("Max links per level")
                .help("Maximum number of links to follow per build")
                .min(1).max(100)
                .value(settings?.maxLinksPerLevel ?: BranchLinksSettings.DEFAULT_MAX_LINKS_PER_LEVEL)
        )

    override fun getId(): String = "branch-links"

    override fun getTitle(): String = "Branch graph"
}