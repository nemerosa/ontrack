package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.labels.MainBuildLinksService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class MainBuildLinksServiceForProjectIT : AbstractGeneralExtensionTestSupport() {

    @Autowired
    protected lateinit var mainBuildLinksService: MainBuildLinksService

    @Test
    fun `Only project settings`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings()
            project {
                setMainBuildLinksProperty(
                    listOf(
                        "type:product", "plugin:pipeline"
                    )
                )
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

    @Test
    fun `Mix of global settings and project settings`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings("type:product", "plugin:pipeline")
            project {
                setMainBuildLinksProperty(
                    listOf(
                        "plugin:pipeline", "plugin:gradle"
                    )
                )
                val config = mainBuildLinksService.getMainBuildLinksConfig(this)
                assertEquals(
                    setOf(
                        "type:product", "plugin:pipeline", "plugin:gradle"
                    ),
                    config.labels.toSet()
                )
            }
        }
    }

    @Test
    fun `Project settings overriding the global settings`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings("type:product", "plugin:pipeline")
            project {
                setMainBuildLinksProperty(
                    overrideGlobal = true,
                    labels = listOf(
                        "plugin:pipeline", "plugin:gradle"
                    )
                )
                val config = mainBuildLinksService.getMainBuildLinksConfig(this)
                assertEquals(
                    setOf(
                        "plugin:pipeline", "plugin:gradle"
                    ),
                    config.labels.toSet()
                )
            }
        }
    }

}