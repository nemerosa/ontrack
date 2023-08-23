package net.nemerosa.ontrack.extension.scm.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SCMRefTest {

    @Test
    fun `Missing protocol`() {
        assertNull(SCMRef.parseUri("//test/config/ref"))
    }

    @Test
    fun `Wrong protocol`() {
        assertNull(SCMRef.parseUri("https://github.com/nemerosa/ontrack/some/path"))
    }

    @Test
    fun `GitHub URI`() {
        assertEquals(
            SCMRef(
                protocol = SCMRef.PROTOCOL,
                type = "github",
                config = "github.com",
                ref = "nemerosa/ontrack/some/path"
            ),
            SCMRef.parseUri("scm://github/github.com/nemerosa/ontrack/some/path")
        )
    }

    @Test
    fun `Test URI`() {
        assertEquals(
            SCMRef(
                protocol = SCMRef.PROTOCOL,
                type = "test",
                config = "my-config",
                ref = "some/path"
            ),
            SCMRef.parseUri("scm://test/my-config/some/path")
        )
    }

}