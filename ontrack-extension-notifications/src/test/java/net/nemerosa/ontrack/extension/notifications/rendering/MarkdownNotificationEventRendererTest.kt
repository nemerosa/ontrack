package net.nemerosa.ontrack.extension.notifications.rendering

import net.nemerosa.ontrack.model.events.MarkdownEventRenderer
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MarkdownNotificationEventRendererTest {

    private val markdownEventRenderer = MarkdownEventRenderer(
        OntrackConfigProperties().apply {
            url = "https://ontrack.nemerosa.net"
        }
    )

    @Test
    fun `Value rendering`() {
        assertEquals(
            "**Some value**",
            markdownEventRenderer.renderStrong("Some value")
        )
    }

    @Test
    fun `Link rendering`() {
        val text = markdownEventRenderer.renderLink("PRJ", "https://ontrack.nemerosa.net/#/project/1")
        assertEquals(
            """[PRJ](https://ontrack.nemerosa.net/#/project/1)""",
            text
        )
    }

    @Test
    fun `Branch link`() {
        val branch = BranchFixtures.testBranch()
        val text = markdownEventRenderer.render(branch, branch.name)
        assertEquals(
            """[${branch.name}](https://ontrack.nemerosa.net/#/branch/${branch.id})""",
            text
        )
    }

}