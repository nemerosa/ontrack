package net.nemerosa.ontrack.extension.av.event

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.extension.notifications.subscriptions.matchesKeywords
import net.nemerosa.ontrack.extension.scm.service.SCMTestFixtures
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AutoVersioningEventServiceImplTest {

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
    fun `Success event matching`() {
        val sourceProject = uid("src-prj-")
        val targetProject = uid("tar-prj-")
        val targetBranch = uid("tar-bch-")
        val event = autoVersioningEventService.success(
            order = createOrder(
                sourceProjectName = sourceProject,
                targetProjectName = targetProject,
                targetBranchName = targetBranch,
            ),
            message = "Reason of success",
            pr = SCMTestFixtures.createSCMPullRequest(),
        )
        val keywords = "$sourceProject $targetProject $targetBranch"
        assertTrue(
            event.matchesKeywords(keywords),
            "Error event matches the keywords"
        )
    }

    @Test
    fun `Error event matching`() {
        val sourceProject = uid("src-prj-")
        val targetProject = uid("tar-prj-")
        val targetBranch = uid("tar-bch-")
        val event = autoVersioningEventService.error(
            order = createOrder(
                sourceProjectName = sourceProject,
                targetProjectName = targetProject,
                targetBranchName = targetBranch,
            ),
            message = "Reason of failure",
            error = RuntimeException("Boom"),
        )
        val keywords = "$sourceProject $targetProject $targetBranch"
        assertTrue(
            event.matchesKeywords(keywords),
            "Error event matches the keywords"
        )
    }

    @Test
    fun `PR merge timeout event matching`() {
        val sourceProject = uid("src-prj-")
        val targetProject = uid("tar-prj-")
        val targetBranch = uid("tar-bch-")
        val event = autoVersioningEventService.prMergeTimeoutError(
            order = createOrder(
                sourceProjectName = sourceProject,
                targetProjectName = targetProject,
                targetBranchName = targetBranch,
            ),
            pr = SCMTestFixtures.createSCMPullRequest(),
        )
        val keywords = "$sourceProject $targetProject $targetBranch"
        assertTrue(
            event.matchesKeywords(keywords),
            "Error event matches the keywords"
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
                Auto versioning of <a href="http://localhost:8080/#/project/1">target</a>/<a href="http://localhost:8080/#/branch/10">main</a> for dependency <a href="http://localhost:8080/#/project/2">source</a> version "2.0.0" has been done.
                
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
                Auto versioning of <a href="http://localhost:8080/#/project/1">target</a>/<a href="http://localhost:8080/#/branch/10">main</a> for dependency <a href="http://localhost:8080/#/project/2">source</a> version "2.0.0" has failed.
                
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
                Auto versioning of <a href="http://localhost:8080/#/project/1">target</a>/<a href="http://localhost:8080/#/branch/10">main</a> for dependency <a href="http://localhost:8080/#/project/2">source</a> version "2.0.0" has failed.
                
                Timeout while waiting for the PR to be ready to be merged.
                
                Pull request <a href="https://github.com/org/repo/pulls/1">PR-1</a>
            """.trimIndent(),
            text
        )
    }

    private fun createOrder(
        sourceProjectName: String = "source",
        targetProjectName: String = "target",
        targetBranchName: String = "main",
    ): AutoVersioningOrder {
        val targetProject = Project.of(nd(targetProjectName, "")).withId(ID.of(1))
        val targetBranch = Branch.of(targetProject, nd(targetBranchName, "")).withId(ID.of(10))

        val sourceProject = Project.of(nd(sourceProjectName, "")).withId(ID.of(2))
        every { structureService.findProjectByName(sourceProjectName) } returns Optional.of(sourceProject)

        return targetBranch.createOrder(
            sourceProject = sourceProjectName,
        )
    }

    private val renderer = HtmlNotificationEventRenderer(
        OntrackConfigProperties()
    )

}