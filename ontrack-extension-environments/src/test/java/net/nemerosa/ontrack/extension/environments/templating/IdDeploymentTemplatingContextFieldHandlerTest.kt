package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotTestFixtures
import net.nemerosa.ontrack.model.events.MarkdownEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IdDeploymentTemplatingContextFieldHandlerTest {

    @Test
    fun `Deployment id`() {

        val handler = IdDeploymentTemplatingContextFieldHandler()

        val deployment = SlotTestFixtures.testDeployment()

        val text = handler.render(
            deployment = deployment,
            config = emptyMap(),
            renderer = MarkdownEventRenderer(OntrackConfigProperties()),
        )

        assertEquals(
            deployment.id,
            text
        )
    }

}