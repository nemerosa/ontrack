package net.nemerosa.ontrack.extension.environments.templating

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotTestFixtures
import net.nemerosa.ontrack.extension.environments.storage.SlotPipelineRepository
import net.nemerosa.ontrack.model.events.MarkdownEventRenderer
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.templating.TemplatingContextHandlerFieldNotManagedException
import net.nemerosa.ontrack.ui.controller.UILocations
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeploymentTemplatingContextHandlerTest {

    @Test
    fun `Rendering of simple fields for a deployment`() {
        doTest(
            field = null
        ) { deployment ->
            "[${deployment.fullName()}](http://localhost:8080/extension/environments/pipeline/${deployment.id})"
        }

        doTest(
            field = "link"
        ) { deployment ->
            "[${deployment.fullName()}](http://localhost:8080/extension/environments/pipeline/${deployment.id})"
        }

        doTest(
            field = "name"
        ) { deployment ->
            deployment.fullName()
        }

        doTest(
            field = "id"
        ) { deployment ->
            deployment.id
        }

        doTest(
            field = "number"
        ) { deployment ->
            deployment.number.toString()
        }

        assertFailsWith<TemplatingContextHandlerFieldNotManagedException> {
            doTest(
                field = "xxx"
            ) { deployment ->
                deployment.id
            }
        }
    }

    private fun doTest(
        field: String?,
        expected: (deployment: SlotPipeline) -> String,
    ) {
        val slotPipelineRepository = mockk<SlotPipelineRepository>()
        val uiLocations = mockk<UILocations>()
        every { uiLocations.page(any()) } answers {
            val path = it.invocation.args[0] as String
            "http://localhost:8080/${path.trimStart('/')}"
        }

        val build = BuildFixtures.testBuild()
        val slot = SlotTestFixtures.testSlot(project = build.project)
        val deployment = SlotPipeline(
            slot = slot,
            build = build,
            number = 15,
        )
        every { slotPipelineRepository.getPipelineById(deployment.id) } returns deployment

        val handler = DeploymentTemplatingContextHandler(
            slotPipelineRepository = slotPipelineRepository,
            uiLocations = uiLocations,
        )

        val text = handler.render(
            data = DeploymentTemplatingContextData(deployment.id),
            field = field,
            config = emptyMap(),
            renderer = MarkdownEventRenderer(OntrackConfigProperties()),
        )

        assertEquals(
            expected(deployment),
            text,
        )
    }

}