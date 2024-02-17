package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LabelBuildDisplayNameExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var labelBuildDisplayNameExtension: LabelBuildDisplayNameExtension

    @Test
    fun `Build with release property with default project configuration`() {
        asAdmin {
            project {
                branch {
                    build {
                        releaseProperty(this, "1.0.0")

                        val displayName = labelBuildDisplayNameExtension.getBuildDisplayName(this)
                        assertEquals("1.0.0", displayName)
                    }
                }
            }
        }
    }

}