package net.nemerosa.ontrack.extension.av.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SameReleaseBranchSourceTest {

    private lateinit var branchDisplayNameService: BranchDisplayNameService
    private lateinit var structureService: StructureService
    private lateinit var sameReleaseBranchSource: SameReleaseBranchSource

    @BeforeEach
    fun init() {

        branchDisplayNameService = mockk()
        every { branchDisplayNameService.getBranchDisplayName(any()) } answers {
            val branch = it.invocation.args[0] as Branch
            branch.name
        }

        structureService = mockk()

        sameReleaseBranchSource = SameReleaseBranchSource(
            branchDisplayNameService = branchDisplayNameService,
            sameBranchSource = SameBranchSource(structureService),
            regexBranchSource = RegexBranchSource(
                structureService = structureService,
                branchDisplayNameService = branchDisplayNameService,
            ),
        )
    }

    @Test
    fun levels() {
        val dependencyProject = ProjectFixtures.testProject()
        val dependency2410 = BranchFixtures.testBranch(id = 11, project = dependencyProject, "release/1.24.10")
        val dependency2411 = BranchFixtures.testBranch(id = 12, project = dependencyProject, "release/1.24.11")
        val dependency2500 = BranchFixtures.testBranch(id = 20, project = dependencyProject, "release/1.25.0")
        val dependencyXXXX = BranchFixtures.testBranch(id = 20, project = dependencyProject, "release/2.0.0")

        every { structureService.getBranchesForProject(dependencyProject.id) } returns listOf(
            dependency2410,
            dependency2411,
            dependency2500,
            dependencyXXXX,
        )

        val target = BranchFixtures.testBranch(name = "release/1.24.6")
        assertEquals(dependency2411, sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target))
        assertEquals(dependency2500, sameReleaseBranchSource.getLatestBranch("1", dependencyProject, target))
        assertEquals(dependency2500, sameReleaseBranchSource.getLatestBranch("", dependencyProject, target))
        assertEquals(dependency2500, sameReleaseBranchSource.getLatestBranch("", dependencyProject, target))
    }

    @Test
    fun `Old versions must not be targeted by newer versions`() {
        val dependencyProject = ProjectFixtures.testProject()
        val dependency1250 = BranchFixtures.testBranch(id = 10, project = dependencyProject, "release/1.25.0")
        val dependency1251 = BranchFixtures.testBranch(id = 11, project = dependencyProject, "release/1.25.1")
        val dependency1260 = BranchFixtures.testBranch(id = 12, project = dependencyProject, "release/1.26.0")

        every { structureService.getBranchesForProject(dependencyProject.id) } returns listOf(
            dependency1250,
            dependency1251,
            dependency1260,
        )

        val targetProject = ProjectFixtures.testProject()
        val target1250 = BranchFixtures.testBranch(id = 20, project = targetProject, "release/1.25.0")
        val target1251 = BranchFixtures.testBranch(id = 21, project = targetProject, "release/1.25.1")
        val target1260 = BranchFixtures.testBranch(id = 22, project = targetProject, "release/1.26.0")

        assertEquals(
            dependency1260,
            sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target1260)
        )

        assertEquals(
            dependency1251,
            sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target1251)
        )

        assertEquals(
            dependency1251,
            sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target1250)
        )
    }

    @Test
    fun `Different levels`() {
        val dependencyProject = ProjectFixtures.testProject()
        val dependency1250 = BranchFixtures.testBranch(id = 10, project = dependencyProject, "release/1.25.0")
        val dependency1260 = BranchFixtures.testBranch(id = 10, project = dependencyProject, "release/1.26.0")
        val dependency1261 = BranchFixtures.testBranch(id = 11, project = dependencyProject, "release/1.26.1")

        every { structureService.getBranchesForProject(dependencyProject.id) } returns listOf(
            dependency1250,
            dependency1260,
            dependency1261,
        )

        val target = BranchFixtures.testBranch(name = "release/1.26")
        assertEquals(dependency1261, sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target))
        assertEquals(dependency1261, sameReleaseBranchSource.getLatestBranch("1", dependencyProject, target))
        assertEquals(dependency1261, sameReleaseBranchSource.getLatestBranch("", dependencyProject, target))
    }

}