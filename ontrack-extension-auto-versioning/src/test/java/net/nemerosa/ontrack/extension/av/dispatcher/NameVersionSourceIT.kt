package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.extension.general.useLabel
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.datasource.embedded.DataSourceFactory
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

}