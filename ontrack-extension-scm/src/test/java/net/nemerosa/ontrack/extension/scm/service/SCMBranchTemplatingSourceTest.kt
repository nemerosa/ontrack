package net.nemerosa.ontrack.extension.scm.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SCMBranchTemplatingSourceTest {

    private lateinit var scmBranchTemplatingSource: SCMBranchTemplatingSource
    private lateinit var scmDetector: SCMDetector
    private lateinit var scm: SCM

    @BeforeEach
    fun init() {
        scm = mockk()
        scmDetector = mockk()
        scmBranchTemplatingSource = SCMBranchTemplatingSource(
            scmDetector,
        )
    }

    @Test
    fun `Extension if branch configured`() {
        val branch = BranchFixtures.testBranch()

        every {
            scm.getSCMBranch(branch)
        } returns "release/1.23"

        every {
            scmDetector.getSCM(branch.project)
        } returns scm

        val value = scmBranchTemplatingSource.render(branch, emptyMap(), PlainEventRenderer.INSTANCE)

        assertEquals(
            "release/1.23",
            value
        )
    }

    @Test
    fun `No extension if branch not configured`() {
        val branch = BranchFixtures.testBranch()

        every {
            scm.getSCMBranch(branch)
        } returns null

        every {
            scmDetector.getSCM(branch.project)
        } returns scm

        val value = scmBranchTemplatingSource.render(branch, emptyMap(), PlainEventRenderer.INSTANCE)

        assertEquals(
            "",
            value
        )
    }

    @Test
    fun `No extension if project not configured`() {
        val branch = BranchFixtures.testBranch()

        every {
            scmDetector.getSCM(branch.project)
        } returns null

        val value = scmBranchTemplatingSource.render(branch, emptyMap(), PlainEventRenderer.INSTANCE)

        assertEquals(
            "",
            value
        )
    }

    @Test
    fun `No extension if not a branch`() {
        val project = ProjectFixtures.testProject()

        val value = scmBranchTemplatingSource.render(project, emptyMap(), PlainEventRenderer.INSTANCE)

        assertEquals(
            "",
            value
        )
    }

}