package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class MainBuildLinksConfigSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<MainBuildLinksConfig>(
    "main-build-links",
    MainBuildLinksConfig::class,
    settingsManagerService,
    cachedSettingsService
) {

    override val type: CascType = cascObject(
        "Configuration which describes the list of build links to display, based on some project labels.",
        cascField(
            MainBuildLinksConfig::labels,
            cascArray(
                "List of project labels to keep as \"main\" dependencies",
                cascString
            )
        )
    )
}