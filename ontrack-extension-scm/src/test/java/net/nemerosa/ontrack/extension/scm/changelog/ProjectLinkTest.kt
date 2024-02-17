package net.nemerosa.ontrack.extension.scm.changelog

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProjectLinkTest {

    @Test
    fun `Project name only`() {
        assertEquals(
            ProjectLink(
                project = "my-project",
                qualifier = ""
            ),
            ProjectLink.parse("my-project")
        )
    }

    @Test
    fun `Project name and qualifier`() {
        assertEquals(
            ProjectLink(
                project = "my-project",
                qualifier = "production"
            ),
            ProjectLink.parse("my-project:production")
        )
    }

}