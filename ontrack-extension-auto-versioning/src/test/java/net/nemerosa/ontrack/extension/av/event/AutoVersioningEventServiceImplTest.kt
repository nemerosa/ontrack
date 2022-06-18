package net.nemerosa.ontrack.extension.av.event

import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.notifications.rendering.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.extension.scm.service.SCMTestFixtures
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AutoVersioningEventServiceImplTest {

    private lateinit var autoVersioningEventService: AutoVersioningEventServiceImpl

    @BeforeEach
    fun before() {
        autoVersioningEventService = AutoVersioningEventServiceImpl(
            eventFactory = mockk(),
            eventPostService = mockk(),
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
                Auto versioning of <a href="http://localhost:8080/#/project/1">target</a>/<a href="http://localhost:8080/#/branch/10">main</a> for dependency source version "2.0.0" has been done.
                
                Reason of success.

                Pull request <a href="https://github.com/org/repo/pulls/1">PR-1</a>
            """.trimIndent(),
            text
        )
    }

    @Test
    fun `Error event template`() {
        val event = autoVersioningEventService.error(
            order = createOrder(),
            message = "Reason of failure",
            error = RuntimeException("Boom"),
        )
        val text = event.render(renderer)
        assertEquals(
            """
                Auto versioning of <a href="http://localhost:8080/#/project/1">target</a>/<a href="http://localhost:8080/#/branch/10">main</a> for dependency source version "2.0.0" has failed.
                
                Reason of failure.
                
                Error: Boom.
            """.trimIndent(),
            text
        )
    }

    @Test
    fun `PR merge timeout event template`() {
        val event = autoVersioningEventService.prMergeTimeoutError(
            order = createOrder(),
            pr = SCMTestFixtures.createSCMPullRequest(),
        )
        val text = event.render(renderer)
        assertEquals(
            """
                Auto versioning of <a href="http://localhost:8080/#/project/1">target</a>/<a href="http://localhost:8080/#/branch/10">main</a> for dependency source version "2.0.0" has failed.
                
                Timeout while waiting for the PR to be ready to be merged.
                
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

        return targetBranch.createOrder(
            sourceProject = source,
        )
    }

    private val renderer = HtmlNotificationEventRenderer(
        OntrackConfigProperties()
    )

}