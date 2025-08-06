package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.extension.general.useLabel
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@AsAdminTest
class DefaultVersionSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var versionSourceFactory: VersionSourceFactory

    @Autowired
    private lateinit var source: DefaultVersionSource

    @Test
    fun `No project BuildLinkDisplayProperty, no build ReleaseProperty`() {
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
    fun `No project BuildLinkDisplayProperty, build ReleaseProperty set`() {
        project {
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
    fun `Project BuildLinkDisplayProperty false, no build ReleaseProperty`() {
        project {
            useLabel(this, useLabel = false)
            branch {
                build {
                    val version = source.getVersion(this, null)
                    assertEquals(name, version)
                }
            }
        }
    }

    @Test
    fun `Project BuildLinkDisplayProperty false, build ReleaseProperty set`() {
        project {
            useLabel(this, useLabel = false)
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
    fun `Project BuildLinkDisplayProperty true, no build ReleaseProperty`() {
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
    fun `Project BuildLinkDisplayProperty true, build ReleaseProperty set`() {
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
    fun `Build name by default`() {
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
    fun `Finding build by label and then by name`() {
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
                val otherBuild = build("4")
                val source = versionSourceFactory.getVersionSource("default")
                assertEquals(sourceBuild, source.getBuildFromVersion(project, null, "1.0.2"))
                assertEquals(sourceBuild, source.getBuildFromVersion(project, null, "2"))
                assertEquals(null, source.getBuildFromVersion(project, null, "1.0.4"))
                assertEquals(otherBuild, source.getBuildFromVersion(project, null, "4"))
                assertEquals(null, source.getBuildFromVersion(project, null, "1.0.5"))
                assertEquals(null, source.getBuildFromVersion(project, null, "5"))
            }
        }
    }

}