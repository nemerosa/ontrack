package net.nemerosa.ontrack.model.files

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileRefTest {

    @Test
    fun `Missing protocol`() {
        assertNull(FileRef.parseUri("//test/config/ref"))
    }

    @Test
    fun `GitHub URI`() {
        assertEquals(
            FileRef(
                protocol = "scm",
                path = "//github/github.com/nemerosa/ontrack/some/path"
            ),
            FileRef.parseUri("scm://github/github.com/nemerosa/ontrack/some/path")
        )
    }

    @Test
    fun `Test URI`() {
        assertEquals(
            FileRef(
                protocol = "scm",
                path = "//test/my-config/some/path"
            ),
            FileRef.parseUri("scm://test/my-config/some/path")
        )
    }

}