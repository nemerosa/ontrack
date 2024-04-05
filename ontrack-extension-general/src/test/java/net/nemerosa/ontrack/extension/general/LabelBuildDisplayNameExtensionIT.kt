package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

    @Test
    fun `Getting a build only using its display name`() {
        asAdmin {
            project {
                branch {
                    build {
                        releaseProperty(this, "1.0.0")

                        assertNotNull(
                            labelBuildDisplayNameExtension.findBuildByDisplayName(
                                project,
                                "1.0.0",
                                onlyDisplayName = true
                            )
                        ) {
                            assertEquals(it, this)
                        }

                        assertNull(
                            labelBuildDisplayNameExtension.findBuildByDisplayName(project, name, onlyDisplayName = true)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting a build using its display name or its name`() {
        asAdmin {
            project {
                branch {
                    build {
                        releaseProperty(this, "1.0.0")

                        assertNotNull(
                            labelBuildDisplayNameExtension.findBuildByDisplayName(
                                project,
                                "1.0.0",
                                onlyDisplayName = false
                            )
                        ) {
                            assertEquals(it, this)
                        }

                        assertNotNull(
                            labelBuildDisplayNameExtension.findBuildByDisplayName(
                                project,
                                name,
                                onlyDisplayName = false
                            )
                        ) {
                            assertEquals(it, this)
                        }
                    }
                }
            }
        }
    }

}