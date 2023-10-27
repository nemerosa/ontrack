package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.extension.general.useLabel
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LabelOnlyVersionSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var versionSourceFactory: VersionSourceFactory

    private lateinit var source: VersionSource

    @BeforeEach
    fun init() {
        source = versionSourceFactory.getVersionSource("labelOnly")
    }

    @Test
    fun `Build name`() {
        project {
            branch {
                build {
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, null)
                    }
                }
            }
        }
    }

    @Test
    fun `Build label but not configured for label`() {
        project {
            branch {
                build {
                    val label = uid("v_")
                    releaseProperty(this, label)
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, null)
                    }
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

    @Test
    fun `Finding build by label only`() {
        project {
            branch {
                build("1") {
                    releaseProperty(this, "1.0.1")
                }
                val sourceBuild = build("2") {
                    releaseProperty(this, "1.0.2")
                }
                build("3") {
                    releaseProperty(this, "1.0.3")
                }
                build("4")
                val source = versionSourceFactory.getVersionSource("labelOnly")
                assertEquals(sourceBuild, source.getBuildFromVersion(project, null, "1.0.2"))
                assertEquals(null, source.getBuildFromVersion(project, null, "2"))
                assertEquals(null, source.getBuildFromVersion(project, null, "1.0.4"))
                assertEquals(null, source.getBuildFromVersion(project, null, "4"))
                assertEquals(null, source.getBuildFromVersion(project, null, "1.0.5"))
                assertEquals(null, source.getBuildFromVersion(project, null, "5"))
            }
        }
    }

}