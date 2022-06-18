package net.nemerosa.ontrack.extension.av.event

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.notifications.rendering.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.extension.scm.service.SCMTestFixtures
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class AutoVersioningEventServiceTest {

    private lateinit var structureService: StructureService

    private lateinit var autoVersioningEventService: AutoVersioningEventServiceImpl

    @BeforeEach
    fun before() {
        structureService = mockk()
        autoVersioningEventService = AutoVersioningEventServiceImpl(
            eventFactory = mockk(),
            eventPostService = mockk(),
            structureService = structureService,
        )
    }

    @Test
    fun `Success event template`() {
        val event = autoVersioningEventService.success(
            order = createOrder(),
            message = "Reason of success",
            pr = SCMTestFixtures.createSCMPullRequest(),
        )
        val text = event.render(renderer)
        assertEquals(
            """
                Auto versioning of <a href="http://localhost:8080/#/branch/10">main</a> for dependency <a href="http://localhost:8080/#/project/2">source</a> version "2.0.0" has been done.

                Pull request <a href="https://github.com/org/repo/pulls/1">PR-1</a>
            """.trimIndent(),
            text
        )
    }

    private fun createOrder(
        source: String = "source",
    ): AutoVersioningOrder {
        val targetProject = Project.of(nd("target", "")).withId(ID.of(1))
        val targetBranch = Branch.of(targetProject, nd("main", "")).withId(ID.of(10))

        val sourceProject = Project.of(nd(source, "")).withId(ID.of(2))
        every { structureService.findProjectByName(source) } returns Optional.of(sourceProject)

        return targetBranch.createOrder(
            sourceProject = source,
        )
    }

    private val renderer = HtmlNotificationEventRenderer(
        OntrackConfigProperties()
    )

}