package net.nemerosa.ontrack.extension.notifications.core

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class YontrackBuildNotificationHelperTest {

    private lateinit var helper: YontrackBuildNotificationHelper
    private lateinit var structureService: StructureService

    @BeforeEach
    fun before() {
        structureService = mockk()
        helper = YontrackBuildNotificationHelper(
            structureService = structureService,
            buildDisplayNameService = mockk(),
        )
    }

    @Test
    fun `getBranch with already-normalized name finds the branch`() {
        val project = ProjectFixtures.testProject()
        val branch = BranchFixtures.testBranch(name = "release-1.0", project = project)
        val event = mockk<Event>()

        every { structureService.findProjectByName(project.name) } returns Optional.of(project)
        every { structureService.findBranchByName(project.name, "release-1.0") } returns Optional.of(branch)

        val result = helper.getBranch(event = event, projectName = project.name, branchName = "release-1.0")
        assertEquals(branch, result)
    }

    @Test
    fun `getBranch with SCM-style name normalizes slashes to dashes`() {
        val project = ProjectFixtures.testProject()
        val branch = BranchFixtures.testBranch(name = "release-1.0", project = project)
        val event = mockk<Event>()

        every { structureService.findProjectByName(project.name) } returns Optional.of(project)
        every { structureService.findBranchByName(project.name, "release-1.0") } returns Optional.of(branch)

        // SCM-style branch name with slash — must be normalized before lookup
        val result = helper.getBranch(event = event, projectName = project.name, branchName = "release/1.0")
        assertEquals(branch, result)
    }

    @Test
    fun `getBranch throws BranchNotFoundException when normalized name does not exist`() {
        val project = ProjectFixtures.testProject()
        val event = mockk<Event>()

        every { structureService.findProjectByName(project.name) } returns Optional.of(project)
        every { structureService.findBranchByName(project.name, "release-99.0") } returns Optional.empty()

        assertFailsWith<BranchNotFoundException> {
            helper.getBranch(event = event, projectName = project.name, branchName = "release/99.0")
        }
    }

}
