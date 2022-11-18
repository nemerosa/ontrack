package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.support.StorageService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class JenkinsPipelineLibraryIndicatorSettingsIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var storageService: StorageService

    @Test
    fun `Default settings`() {
        storageService.deleteWithFilter(store = JenkinsPipelineLibraryIndicatorSettings::class.java.name)
        cachedSettingsService.invalidate(JenkinsPipelineLibraryIndicatorSettings::class.java)
        val settings = cachedSettingsService.getCachedSettings(JenkinsPipelineLibraryIndicatorSettings::class.java)
        assertTrue(settings.libraryVersions.isEmpty(), "No library requirement by default")
    }

    @Test
    fun `Saving and retrieving the settings`() {
        storageService.deleteWithFilter(store = JenkinsPipelineLibraryIndicatorSettings::class.java.name)
        val settings = JenkinsPipelineLibraryIndicatorSettings(
            libraryVersions = listOf(
                JenkinsPipelineLibraryIndicatorLibrarySettings(
                    library = "pipeline-library",
                    required = false,
                    lastSupported = "5",
                    lastDeprecated = "4",
                    lastUnsupported = "3",
                )
            )
        )
        asAdmin {
            settingsManagerService.saveSettings(settings)
            val loaded = cachedSettingsService.getCachedSettings(JenkinsPipelineLibraryIndicatorSettings::class.java)
            assertEquals(settings, loaded)
        }
    }

}