package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.settings.HomePageSettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class CascServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascService: CascService

    @Test
    fun rendering() {
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

}