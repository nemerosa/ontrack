package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.extension.general.useLabel
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DefaultVersionSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var source: DefaultVersionSource

    @Test
    fun `Build name`() {
        project {
            branch {
                build {
                    val version = source.getVersion(this, null)
                    assertEquals(name, version)
                }
            }
        }
    }

    @Test
    fun `Build label but not configured for label returns the name`() {
        project {
            branch {
                build {
                    val label = uid("v_")
                    releaseProperty(this, label)
                    val version = source.getVersion(this, null)
                    assertEquals(name, version)
                }
            }
        }
    }

    @Test
    fun `Build label and configured for label`() {
        project {
            useLabel(this)
            branch {
                build {
                    val label = uid("v_")
                    releaseProperty(this, label)
                    val version = source.getVersion(this, null)
                    assertEquals(label, version)
                }
            }
        }
    }

    @Test
    fun `No build label and configured for label`() {
        project {
            useLabel(this)
            branch {
                build {
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, null)
                    }
                }
            }
        }
    }

}