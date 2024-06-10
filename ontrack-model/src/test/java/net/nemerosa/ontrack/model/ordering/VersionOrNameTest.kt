package net.nemerosa.ontrack.model.ordering

import net.nemerosa.ontrack.common.Version
import org.junit.jupiter.api.Test

class VersionOrNameTest {

    @Test
    fun comparisons() {
        kotlin.test.assertTrue(VersionOrName(Version(1, 0, 0)) > VersionOrName(Version(0, 1, 0)))
        kotlin.test.assertTrue(VersionOrName(Version(1, 0, 0)) > VersionOrName("release/0.1"))
        kotlin.test.assertTrue(VersionOrName("release/1.0") > VersionOrName("release/0.1"))
    }

}