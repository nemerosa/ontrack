package net.nemerosa.ontrack.extension.stale

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AutoDisablingBranchPatternsStaleBranchCheckTest {

    private lateinit var propertyService: PropertyService
    private lateinit var check: AutoDisablingBranchPatternsStaleBranchCheck

    @BeforeEach
    fun init() {
        propertyService = mockk()
        check = AutoDisablingBranchPatternsStaleBranchCheck(
            extensionFeature = StaleExtensionFeature(),
            propertyService = propertyService,
            branchOrderingService = mockk(),
        )
    }

    @Test
    fun `When project not configured, not eligible`() {
        val project = ProjectFixtures.testProject()
        every {
            propertyService.hasProperty(
                project,
                AutoDisablingBranchPatternsPropertyType::class.java
            )
        } returns false
        assertFalse(check.isProjectEligible(project), "Project is not eligible")
    }

    @Test
    fun `When project configured, eligible`() {
        val project = ProjectFixtures.testProject()
        every {
            propertyService.hasProperty(
                project,
                AutoDisablingBranchPatternsPropertyType::class.java
            )
        } returns true
        assertTrue(check.isProjectEligible(project), "Project is eligible")
    }

    @Test
    fun `When branch not disabled, eligible`() {
        val branch = BranchFixtures.testBranch(disabled = false)
        assertTrue(check.isBranchEligible(branch), "Branch is eligible")
    }

    @Test
    fun `When branch disabled, not eligible`() {
        val branch = BranchFixtures.testBranch(disabled = true)
        assertFalse(check.isBranchEligible(branch), "Branch is not eligible")
    }

}