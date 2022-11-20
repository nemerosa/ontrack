package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.multiform
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

@Component
class JenkinsPipelineLibraryIndicatorSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    // Note: due to the complex settings class, not using the settings repository
    private val storageService: StorageService,
) : AbstractSettingsManager<JenkinsPipelineLibraryIndicatorSettings>(
    JenkinsPipelineLibraryIndicatorSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: JenkinsPipelineLibraryIndicatorSettings) {
        storageService.store(
            JenkinsPipelineLibraryIndicatorSettings::class.java.name,
            "settings",
            settings
        )
    }

    override fun getSettingsForm(settings: JenkinsPipelineLibraryIndicatorSettings): Form =
        Form.create().multiform(
            JenkinsPipelineLibraryIndicatorSettings::libraryVersions,
            settings.libraryVersions
        ) {
            JenkinsPipelineLibraryIndicatorLibrarySettings.getForm(null)
        }

    override fun getId(): String = "jenkins-pipeline-libraries-indicators"

    override fun getTitle(): String = "Jenkins pipeline libraries indicators"
}