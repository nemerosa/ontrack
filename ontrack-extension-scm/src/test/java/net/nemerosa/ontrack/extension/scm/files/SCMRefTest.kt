package net.nemerosa.ontrack.extension.scm.files

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SCMRefTest {

    @Test
    fun `Wrong format, missing type`() {
        assertNull(SCMRef.parseUri("/github.com/nemerosa/ontrack/some/path"))
    }

    @Test
    fun `GitHub URI`() {
        assertEquals(
            SCMRef(
                type = "github",
                config = "github.com",
                ref = "nemerosa/ontrack/some/path"
            ),
            SCMRef.parseUri("//github/github.com/nemerosa/ontrack/some/path")
        )
    }

    @Test
    fun `Test URI`() {
        assertEquals(
            SCMRef(
                type = "test",
                config = "my-config",
                ref = "some/path"
            ),
            SCMRef.parseUri("//test/my-config/some/path")
        )
    }

}