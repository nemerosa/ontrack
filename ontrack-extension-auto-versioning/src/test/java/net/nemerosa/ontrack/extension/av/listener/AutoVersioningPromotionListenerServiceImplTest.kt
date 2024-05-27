package net.nemerosa.ontrack.extension.av.listener

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.project.AutoVersioningProjectProperty
import net.nemerosa.ontrack.extension.av.project.AutoVersioningProjectPropertyType
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AutoVersioningPromotionListenerServiceImplTest {

    private lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService
    private lateinit var scmDetector: SCMDetector
    private lateinit var propertyService: PropertyService
    private lateinit var structureService: StructureService
    private lateinit var autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService

    @BeforeEach
    fun init() {

        autoVersioningConfigurationService = mockk()

        scmDetector = mockk()
        propertyService = mockk()
        structureService = mockk()

        autoVersioningPromotionListenerService = AutoVersioningPromotionListenerServiceImpl(
            autoVersioningConfigurationService = autoVersioningConfigurationService,
            scmDetector = scmDetector,
            propertyService = propertyService,
            structureService = structureService,
        )
    }

    @Test
    fun `Disabled branches are not targeted by auto-versioning`() {
        val branch = BranchFixtures.testBranch(disabled = true)

        val source = BranchFixtures.testBranch()
        val run = PromotionRunFixtures.testPromotionRun(branch = source)
        val build = run.build
        val promotion = run.promotionLevel

        every {
            autoVersioningConfigurationService.getBranchesConfiguredFor(build.project.name, promotion.name)
        } returns listOf(branch)

        val avBranches = autoVersioningPromotionListenerService.getConfiguredBranches(run)

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
        val run = PromotionRunFixtures.testPromotionRun(branch = source)
        val build = run.build
        val promotion = run.promotionLevel

        every {
            autoVersioningConfigurationService.getBranchesConfiguredFor(build.project.name, promotion.name)
        } returns listOf(branch)

        val avBranches = autoVersioningPromotionListenerService.getConfiguredBranches(run)

        assertNotNull(avBranches) {
            assertTrue(
                it.configuredBranches.isEmpty(),
                "No targeted branch"
            )
        }
    }

    @Test
    fun `Accepting all branches for AV when no configuration as project level`() {
        val branch = BranchFixtures.testBranch()

        every {
            propertyService.getPropertyValue(
                branch.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns null

        assertTrue(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(branch),
            "Branch accepted for AV because project not configured"
        )
    }

    @Test
    fun `Accepting all branches for AV when no inclusion rule`() {
        val branch = BranchFixtures.testBranch()

        every {
            propertyService.getPropertyValue(
                branch.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = null,
            branchExcludes = null,
            lastActivityDate = null,
        )

        assertTrue(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(branch),
            "Branch accepted for AV because no inclusion rule"
        )
    }

    @Test
    fun `Rejecting branch for AV when included`() {
        val branch = BranchFixtures.testBranch(name = "release-2024")

        every {
            propertyService.getPropertyValue(
                branch.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = listOf("release-.*", "main"),
            branchExcludes = null,
            lastActivityDate = null,
        )

        assertTrue(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(branch),
            "Branch accepted for AV because inclusion rule"
        )
    }

    @Test
    fun `Rejecting branch for AV when not included`() {
        val branch = BranchFixtures.testBranch(name = "feature-x")

        every {
            propertyService.getPropertyValue(
                branch.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = listOf("release-.*", "main"),
            branchExcludes = null,
            lastActivityDate = null,
        )

        assertFalse(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(branch),
            "Branch not accepted for AV because inclusion rule"
        )
    }

    @Test
    fun `Accepting branch for AV when not excluded`() {
        val branch = BranchFixtures.testBranch(name = "release-2024")

        every {
            propertyService.getPropertyValue(
                branch.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = null,
            branchExcludes = listOf("PR-.*"),
            lastActivityDate = null,
        )

        assertTrue(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(branch),
            "Branch accepted for AV because not excluded"
        )
    }

    @Test
    fun `Rejecting branch for AV when excluded`() {
        val branch = BranchFixtures.testBranch(name = "PR-2024")

        every {
            propertyService.getPropertyValue(
                branch.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = null,
            branchExcludes = listOf("PR-.*"),
            lastActivityDate = null,
        )

        assertFalse(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(branch),
            "Branch rejected for AV because excluded"
        )
    }

    @Test
    fun `Accepting branch for AV when after threshold time`() {
        val ref = Time.now
        val build = BuildFixtures.testBuild(
            dateTime = ref.minusDays(10)
        )

        every {
            propertyService.getPropertyValue(
                build.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = null,
            branchExcludes = null,
            lastActivityDate = ref.minusDays(14),
        )

        every {
            structureService.getLastBuild(build.branch.id)
        } returns Optional.of(build)

        assertTrue(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(build.branch),
            "Branch accepted for AV because date OK"
        )
    }

    @Test
    fun `Accepting branch for AV when no build`() {
        val ref = Time.now
        val branch = BranchFixtures.testBranch()

        every {
            propertyService.getPropertyValue(
                branch.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = null,
            branchExcludes = null,
            lastActivityDate = ref.minusDays(14),
        )

        every {
            structureService.getLastBuild(branch.id)
        } returns Optional.empty()

        assertTrue(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(branch),
            "Branch accepted for AV because date OK"
        )
    }

    @Test
    fun `Rejecting branch for AV when before threshold time`() {
        val ref = Time.now
        val build = BuildFixtures.testBuild(
            dateTime = ref.minusDays(16)
        )

        every {
            propertyService.getPropertyValue(
                build.project,
                AutoVersioningProjectPropertyType::class.java
            )
        } returns AutoVersioningProjectProperty(
            branchIncludes = null,
            branchExcludes = null,
            lastActivityDate = ref.minusDays(14),
        )

        every {
            structureService.getLastBuild(build.branch.id)
        } returns Optional.of(build)

        assertFalse(
            autoVersioningPromotionListenerService.acceptBranchWithProjectAVRules(build.branch),
            "Branch rejected for AV because date not OK"
        )
    }

}