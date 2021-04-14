package net.nemerosa.ontrack.extension.github.casc

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.extension.github.catalog.GitHubSCMCatalogSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class GitHubSCMCatalogSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<GitHubSCMCatalogSettings>(
    "github-scm-catalog",
    GitHubSCMCatalogSettings::class,
    settingsManagerService,
    cachedSettingsService
) {

    override val type: CascType = cascObject(
        "Settings for collecting SCM Catalog from GitHub.",
        cascField(
            GitHubSCMCatalogSettings::orgs,
            cascArray(
                "List of GitHub organizations to collect.",
                cascString
            )
        )
    )
}