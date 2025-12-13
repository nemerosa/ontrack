package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

@Component
class JenkinsPipelineLibraryIndicatorSettingsProvider(
    // Note: due to the complex settings class, not using the settings repository
    private val storageService: StorageService,
) : SettingsProvider<JenkinsPipelineLibraryIndicatorSettings> {

    override fun getSettings(): JenkinsPipelineLibraryIndicatorSettings {
        return storageService.find(
            store = JenkinsPipelineLibraryIndicatorSettings::class.java.name,
            key = "settings",
            type = JenkinsPipelineLibraryIndicatorSettings::class
        ) ?: JenkinsPipelineLibraryIndicatorSettings(emptyList())
    }

    override fun getSettingsClass(): Class<JenkinsPipelineLibraryIndicatorSettings> =
        JenkinsPipelineLibraryIndicatorSettings::class.java
}