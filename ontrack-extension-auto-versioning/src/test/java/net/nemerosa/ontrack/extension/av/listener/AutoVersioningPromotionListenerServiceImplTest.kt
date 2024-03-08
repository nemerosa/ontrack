package net.nemerosa.ontrack.extension.av.listener

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import net.nemerosa.ontrack.model.structure.PromotionLevelFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AutoVersioningPromotionListenerServiceImplTest {

    private lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService
    private lateinit var scmDetector: SCMDetector
    private lateinit var autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService

    @BeforeEach
    fun init() {

        autoVersioningConfigurationService = mockk()

        scmDetector = mockk()

        autoVersioningPromotionListenerService = AutoVersioningPromotionListenerServiceImpl(
            autoVersioningConfigurationService = autoVersioningConfigurationService,
            scmDetector = scmDetector,
        )
    }

    @Test
    fun `Disabled branches are not targeted by auto-versioning`() {
        val branch = BranchFixtures.testBranch(disabled = true)

        val source = BranchFixtures.testBranch()
        val build = BuildFixtures.testBuild(branch = source)
        val promotion = PromotionLevelFixtures.testPromotionLevel(branch = source)

        every {
            autoVersioningConfigurationService.getBranchesConfiguredFor(build.project.name, promotion.name)
        } returns listOf(branch)

        val avBranches = autoVersioningPromotionListenerService.getConfiguredBranches(build, promotion)

        assertNotNull(avBranches) {
            assertTrue(
                it.configuredBranches.isEmpty(),
                "No targeted branch"
            )
        }
    }

    @Test
    fun `Disabled projects are not targeted by auto-versioning`() {
        val project = ProjectFixtures.testProject(disabled = true)
        val branch = BranchFixtures.testBranch(project = project)

        val source = BranchFixtures.testBranch()
        val build = BuildFixtures.testBuild(branch = source)
        val promotion = PromotionLevelFixtures.testPromotionLevel(branch = source)

        every {
            autoVersioningConfigurationService.getBranchesConfiguredFor(build.project.name, promotion.name)
        } returns listOf(branch)

        val avBranches = autoVersioningPromotionListenerService.getConfiguredBranches(build, promotion)

        assertNotNull(avBranches) {
            assertTrue(
                it.configuredBranches.isEmpty(),
                "No targeted branch"
            )
        }
    }

}