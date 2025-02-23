package net.nemerosa.ontrack.extension.environments.templating

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestFixtures
import net.nemerosa.ontrack.extension.environments.storage.SlotPipelineRepository
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingService
import net.nemerosa.ontrack.model.events.MarkdownEventRenderer
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChangelogDeploymentTemplatingContextFieldHandlerTest {

    @Test
    fun `Changelog for a deployment`() {

        val renderer = MarkdownEventRenderer(OntrackConfigProperties())

        val deployment = SlotTestFixtures.testDeployment()

        val previousDeployment = SlotTestFixtures.testDeployment(
            build = BuildFixtures.testBuild(
                branch = deployment.build.branch,
            ),
            slot = deployment.slot,
        )

        val slotPipelineRepository = mockk<SlotPipelineRepository>()
        every {
            slotPipelineRepository.findLastPipelineBySlotAndStatusExcludingOne(
                slot = deployment.slot,
                status = SlotPipelineStatus.DONE,
                excludedPipeline = deployment,
            )
        } returns previousDeployment

        val changeLogTemplatingService = mockk<ChangeLogTemplatingService>()
        every {
            changeLogTemplatingService.render(
                fromBuild = previousDeployment.build,
                toBuild = deployment.build,
                configMap = emptyMap(),
                renderer = renderer,
            )
        } returns "* ISS-31 Security defect"

        val handler = ChangelogDeploymentTemplatingContextFieldHandler(
            slotPipelineRepository = slotPipelineRepository,
            changeLogTemplatingService = changeLogTemplatingService,
        )

        val text = handler.render(
            deployment = deployment,
            config = emptyMap(),
            renderer = renderer,
        )

        assertEquals(
            "* ISS-31 Security defect",
            text,
        )
    }

}