package net.nemerosa.ontrack.extension.environments.templating

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.environments.SlotTestFixtures
import net.nemerosa.ontrack.model.events.MarkdownEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.ui.controller.UILocations
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LinkDeploymentTemplatingContextFieldHandlerTest {

    @Test
    fun `Deployment link`() {

        val uiLocations = mockk<UILocations>()
        every { uiLocations.page(any()) } answers {
            val path = it.invocation.args[0] as String
            "http://localhost:8080/${path.trimStart('/')}"
        }

        val handler = LinkDeploymentTemplatingContextFieldHandler(
            uiLocations = uiLocations,
        )

        val deployment = SlotTestFixtures.testDeployment()

        val text = handler.render(
            deployment = deployment,
            config = emptyMap(),
            renderer = MarkdownEventRenderer(OntrackConfigProperties()),
        )

        assertEquals(
            "[${deployment.fullName()}](http://localhost:8080/extension/environments/pipeline/${deployment.id})",
            text
        )
    }

}