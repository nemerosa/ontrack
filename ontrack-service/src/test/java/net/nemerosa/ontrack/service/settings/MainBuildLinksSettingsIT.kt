package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import org.junit.Test
import kotlin.test.assertEquals

class MainBuildLinksSettingsIT : AbstractDSLTestJUnit4Support() {

    @Test
    fun `Setting and restoring the main build settings`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings(
                    "type:plugin",
                    "plugin:ontrack"
            )
            val labels = mainBuildLinksSettings
            assertEquals(
                    setOf(
                            "type:plugin",
                            "plugin:ontrack"
                    ),
                    labels.toSet()
            )
        }
    }

}