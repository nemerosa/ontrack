package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.settings.HomePageSettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CascServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascService: CascService

    @Test
    fun `Rendering as YAML`() {
        asAdmin {
            withSettings<HomePageSettings> {
                settingsManagerService.saveSettings(
                    HomePageSettings(
                        maxBranches = 2,
                        maxProjects = 200,
                    )
                )
                val yaml = cascService.renderAsYaml()
                assertTrue("maxBranches: 2" in yaml)
                assertTrue("maxProjects: 200" in yaml)
            }
        }
    }

    @Test
    fun `Rendering as JSON`() {
        asAdmin {
            withSettings<HomePageSettings> {
                settingsManagerService.saveSettings(
                    HomePageSettings(
                        maxBranches = 2,
                        maxProjects = 200,
                    )
                )
                val json = cascService.renderAsJson()
                assertEquals(2,
                    json.path("ontrack").path("config").path("settings").path("home-page").path("maxBranches").asInt())
                assertEquals(200,
                    json.path("ontrack").path("config").path("settings").path("home-page").path("maxProjects").asInt())
            }
        }
    }

}