package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class NameVersionSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var versionSourceFactory: VersionSourceFactory

    @Test
    fun `Build name`() {
        project {
            branch {
                build {
                    val source = versionSourceFactory.getVersionSource("name")
                    val version = source.getVersion(this, null)
                    assertEquals(name, version)
                }
            }
        }
    }

    @Test
    fun `Finding build by name`() {
        project {
            branch {
                build("1.0.1")
                val sourceBuild = build("1.0.2")
                build("1.0.3")
                val source = versionSourceFactory.getVersionSource("name")
                assertEquals(sourceBuild, source.getBuildFromVersion(project, null, "1.0.2"))
                assertEquals(null, source.getBuildFromVersion(project, null, "1.0.4"))
            }
        }
    }

}