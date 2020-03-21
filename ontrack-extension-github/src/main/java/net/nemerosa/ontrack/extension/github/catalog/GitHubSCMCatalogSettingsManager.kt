package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiStrings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class GitHubSCMCatalogSettingsManager(
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService,
        private val settingsRepository: SettingsRepository
) : AbstractSettingsManager<GitHubSCMCatalogSettings>(
        GitHubSCMCatalogSettings::class.java,
        cachedSettingsService,
        securityService
) {
    override fun doSaveSettings(settings: GitHubSCMCatalogSettings) {
        settingsRepository.setString(
                GitHubSCMCatalogSettings::class.java,
                GitHubSCMCatalogSettings::orgs.name,
                settings.orgs.joinToString("|")
        )
    }

    override fun getId(): String = "github-scm-catalog"

    override fun getTitle(): String = "GitHub SCM Catalog"

    override fun getSettingsForm(settings: GitHubSCMCatalogSettings): Form =
            Form.create()
                    .with(
                            MultiStrings.of(GitHubSCMCatalogSettings::orgs.name)
                                    .label("Organizations")
                                    .help("List of organizations to collect information about.")
                                    .value(settings.orgs)
                    )
}