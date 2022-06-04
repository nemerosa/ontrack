package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.av.AutoVersioningTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class AutoVersioningConfigurationServiceIT : AutoVersioningTestSupport() {

    @Test
    fun `Saving and retrieving a configuration`() {
        asAdmin {
            project {
                branch {
                    assertNull(
                        autoVersioningConfigurationService.getAutoVersioning(this),
                        "No auto versioning initially"
                    )
                    val config = AutoVersioningTestFixtures.sampleConfig()
                    autoVersioningConfigurationService.setupAutoVersioning(this, config)
                    val saved = autoVersioningConfigurationService.getAutoVersioning(this)
                    assertEquals(config, saved)
                }
            }
        }
    }

    @Test
    fun `Deleting a configuration`() {
        asAdmin {
            project {
                branch {
                    val config = AutoVersioningTestFixtures.sampleConfig()
                    autoVersioningConfigurationService.setupAutoVersioning(this, config)
                    autoVersioningConfigurationService.setupAutoVersioning(this, null)
                    val finalOne = autoVersioningConfigurationService.getAutoVersioning(this)
                    assertNull(finalOne, "Configuration has been deleted")
                }
            }
        }
    }

}