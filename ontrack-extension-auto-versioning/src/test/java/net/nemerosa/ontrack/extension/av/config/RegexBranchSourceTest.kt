package net.nemerosa.ontrack.extension.av.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RegexBranchSourceTest {

    private lateinit var branchDisplayNameService: BranchDisplayNameService
    private lateinit var structureService: StructureService
    private lateinit var regexBranchSource: RegexBranchSource

    @BeforeEach
    fun init() {

        branchDisplayNameService = mockk()
        every { branchDisplayNameService.getBranchDisplayName(any()) } answers {
            val branch = it.invocation.args[0] as Branch
            branch.name
        }

        structureService = mockk()

        regexBranchSource = RegexBranchSource(
            structureService = structureService,
            branchDisplayNameService = branchDisplayNameService,
        )
    }

    @Test
    fun `Missing regular expression`() {
        val dependency = BranchFixtures.testBranch()
        val target = BranchFixtures.testBranch()
        assertFailsWith<BranchSourceMissingConfigurationException> {
            regexBranchSource.getLatestBranch(null, dependency.project, target)
        }
    }

    @Test
    fun `Regular expression`() {
        val dependencyProject = ProjectFixtures.testProject()
        val dependency11 = BranchFixtures.testBranch(id = 11, project = dependencyProject, "release/1.11")
        val dependency12 = BranchFixtures.testBranch(id = 12, project = dependencyProject, "release/1.12")
        val dependency20 = BranchFixtures.testBranch(id = 20, project = dependencyProject, "release/1.20")

        every { structureService.getBranchesForProject(dependencyProject.id) } returns listOf(
            dependency20,
            dependency12,
            dependency11,
        )

        val target = BranchFixtures.testBranch()
        val latest = regexBranchSource.getLatestBranch("release/1\\.1.*", dependencyProject, target)
        assertEquals(dependency12, latest)
    }

}