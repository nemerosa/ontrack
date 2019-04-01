package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.labels.MainBuildLinksService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MainBuildLinksServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mainBuildLinksService: MainBuildLinksService

    @Test
    fun `No main build link by default`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings()
            project {
                val config = mainBuildLinksService.getMainBuildLinksConfig(this)
                assertTrue(config.labels.isEmpty(), "No label being configured")
            }
        }
    }

    @Test
    fun `Only global settings`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings("type:product", "plugin:pipeline")
            project {
                val config = mainBuildLinksService.getMainBuildLinksConfig(this)
                assertEquals(
                        setOf(
                                "type:product", "plugin:pipeline"
                        ),
                        config.labels.toSet()
                )
            }
        }
    }

}