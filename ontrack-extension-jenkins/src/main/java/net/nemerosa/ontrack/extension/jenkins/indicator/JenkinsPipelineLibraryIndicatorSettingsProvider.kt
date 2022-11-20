package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.model.support.retrieve
import org.springframework.stereotype.Component

@Component
class JenkinsPipelineLibraryIndicatorSettingsProvider(
    // Note: due to the complex settings class, not using the settings repository
    private val storageService: StorageService,
) : SettingsProvider<JenkinsPipelineLibraryIndicatorSettings> {

    override fun getSettings(): JenkinsPipelineLibraryIndicatorSettings {
        return storageService.retrieve<JenkinsPipelineLibraryIndicatorSettings>(
            JenkinsPipelineLibraryIndicatorSettings::class.java.name,
            "settings"
        ) ?: JenkinsPipelineLibraryIndicatorSettings(emptyList())
    }

    override fun getSettingsClass(): Class<JenkinsPipelineLibraryIndicatorSettings> =
        JenkinsPipelineLibraryIndicatorSettings::class.java
}