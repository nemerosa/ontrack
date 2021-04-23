package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import org.junit.Test
import kotlin.test.assertEquals

class MainBuildLinksConfigSettingsContextIT : AbstractCascTestSupport() {

    @Test
    fun `Main build links config as CasC`() {
        asAdmin {
            withSettings<MainBuildLinksConfig> {
                casc("""
                    ontrack:
                        config:
                            settings:
                                main-build-links:
                                    labels:
                                        - label-1
                                        - label-2
                """.trimIndent())
                val settings = cachedSettingsService.getCachedSettings(MainBuildLinksConfig::class.java)
                assertEquals(
                    listOf(
                        "label-1",
                        "label-2",
                    ),
                    settings.labels
                )
            }
        }
    }

}