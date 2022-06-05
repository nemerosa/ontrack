package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.common.Version
import org.junit.Test
import kotlin.test.assertTrue

class VersionOrNameTest {

    @Test
    fun comparisons() {
        assertTrue(VersionOrName(Version(1, 0, 0)) > VersionOrName(Version(0, 1, 0)))
        assertTrue(VersionOrName(Version(1, 0, 0)) > VersionOrName("release/0.1"))
        assertTrue(VersionOrName("release/1.0") > VersionOrName("release/0.1"))
    }

}