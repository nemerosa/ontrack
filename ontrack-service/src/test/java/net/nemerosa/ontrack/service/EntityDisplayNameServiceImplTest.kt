package net.nemerosa.ontrack.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EntityDisplayNameServiceImplTest {

    private lateinit var branchDisplayNameService: BranchDisplayNameService
    private lateinit var buildDisplayNameService: BuildDisplayNameService
    private lateinit var entityDisplayNameService: EntityDisplayNameService

    @BeforeEach
    fun init() {

        branchDisplayNameService = mockk()
        buildDisplayNameService = mockk()

        entityDisplayNameService = EntityDisplayNameServiceImpl(
            branchDisplayNameService = branchDisplayNameService,
            buildDisplayNameService = buildDisplayNameService,
        )
    }

    @Test
    fun `Project display name`() {
        val project = ProjectFixtures.testProject()
        assertEquals(project.name, entityDisplayNameService.getEntityDisplayName(project))
    }

    @Test
    fun `Branch display name without custom display name`() {
        val branch = BranchFixtures.testBranch()
        every { branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME) } returns branch.name
        assertEquals(branch.name, entityDisplayNameService.getEntityDisplayName(branch))
    }

    @Test
    fun `Branch display name with custom display name`() {
        val branch = BranchFixtures.testBranch()
        every { branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME) } returns "release/1.0"
        assertEquals("release/1.0", entityDisplayNameService.getEntityDisplayName(branch))
    }

    @Test
    fun `Build display name without custom display name`() {
        val build = BuildFixtures.testBuild()
        every { buildDisplayNameService.getBuildDisplayName(build) } returns build.name
        assertEquals(build.name, entityDisplayNameService.getEntityDisplayName(build))
    }

    @Test
    fun `Build display name with custom display name`() {
        val build = BuildFixtures.testBuild()
        every { buildDisplayNameService.getBuildDisplayName(build) } returns "1.0.0"
        assertEquals("1.0.0", entityDisplayNameService.getEntityDisplayName(build))
    }

}