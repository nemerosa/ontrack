package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VersionSourceFactoryIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var versionSourceFactory: VersionSourceFactory

    @Test
    fun `Default version source is always available`() {
        val source = versionSourceFactory.getVersionSource("default")
        assertEquals(DefaultVersionSource.ID, source.id)
    }

    @Test
    fun `Unknown version source`() {
        assertFailsWith<VersionSourceNotFoundException> {
            versionSourceFactory.getVersionSource("xxx")
        }
    }

}