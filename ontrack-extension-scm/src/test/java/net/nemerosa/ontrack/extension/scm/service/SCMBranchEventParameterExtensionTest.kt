package net.nemerosa.ontrack.extension.scm.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SCMBranchEventParameterExtensionTest {

    private lateinit var scmBranchEventParameterExtension: SCMBranchEventParameterExtension
    private lateinit var scmDetector: SCMDetector
    private lateinit var scm: SCM

    @BeforeEach
    fun init() {
        scm = mockk()
        scmDetector = mockk()
        scmBranchEventParameterExtension = SCMBranchEventParameterExtension(
            mockk(),
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

        val parameters = scmBranchEventParameterExtension.additionalTemplateParameters(branch)

        assertEquals(
            mapOf(
                "scmBranch" to "release/1.23"
            ),
            parameters
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

        val parameters = scmBranchEventParameterExtension.additionalTemplateParameters(branch)

        assertEquals(
            emptyMap(),
            parameters
        )
    }

    @Test
    fun `No extension if project not configured`() {
        val branch = BranchFixtures.testBranch()

        every {
            scmDetector.getSCM(branch.project)
        } returns null

        val parameters = scmBranchEventParameterExtension.additionalTemplateParameters(branch)

        assertEquals(
            emptyMap(),
            parameters
        )
    }

    @Test
    fun `No extension if not a branch`() {
        val project = ProjectFixtures.testProject()

        val parameters = scmBranchEventParameterExtension.additionalTemplateParameters(project)

        assertEquals(
            emptyMap(),
            parameters
        )
    }

}