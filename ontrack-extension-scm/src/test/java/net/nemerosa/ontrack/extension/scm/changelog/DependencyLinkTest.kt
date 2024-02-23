package net.nemerosa.ontrack.extension.scm.changelog

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DependencyLinkTest {

    @Test
    fun `Project name only`() {
        assertEquals(
            DependencyLink(
                project = "my-project",
                qualifier = ""
            ),
            DependencyLink.parse("my-project")
        )
    }

    @Test
    fun `Project name and qualifier`() {
        assertEquals(
            DependencyLink(
                project = "my-project",
                qualifier = "production"
            ),
            DependencyLink.parse("my-project:production")
        )
    }

}