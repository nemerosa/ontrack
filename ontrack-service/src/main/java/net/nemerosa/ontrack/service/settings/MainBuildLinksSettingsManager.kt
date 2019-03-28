package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiStrings
import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class MainBuildLinksSettingsManager(
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService,
        private val settingsRepository: SettingsRepository
) : AbstractSettingsManager<MainBuildLinksConfig>(
        MainBuildLinksConfig::class.java,
        cachedSettingsService,
        securityService
) {

    override fun getId(): String = "main-build-links"

    override fun getTitle(): String = "Main build links"

    override fun doSaveSettings(settings: MainBuildLinksConfig?) {
        if (settings != null) {
            settingsRepository.setString(
                    MainBuildLinksConfig::class.java,
                    "labels",
                    settings.labels.joinToString("|")
            )
        }
    }

    override fun getSettingsForm(settings: MainBuildLinksConfig?): Form {
        return Form.create()
                .with(
                        MultiStrings.of("labels")
                                .help("List of project labels identifying the build links to keep in decorations.")
                                .label("Project labels")
                                .value(settings?.labels ?: emptyList<String>())
                )
    }
}