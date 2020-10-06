package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.mocking.GitMockingConfigurator
import net.nemerosa.ontrack.extension.stale.StaleJobService
import net.nemerosa.ontrack.extension.stale.StaleProperty
import net.nemerosa.ontrack.extension.stale.StalePropertyType
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Integration tests between the PR check and the normal branch check.
 *
 * Combinations:
 *
 * | PR status | Stale property | Last build | Expected result |
 * |---|---|---|---|
 * | Valid PR | No property | Recent | Kept |
 * | Valid PR | No property | Medium | Kept |
 * | Valid PR | No property | Old | Kept |
 * | Valid PR | Property w/o promotion | Recent | Kept |
 * | Valid PR | Property w/o promotion | Medium | Disabled |
 * | Valid PR | Property w/o promotion | Old | Deleted |
 * | Valid PR | Property w/ promotion and not promoted | Recent | Kept |
 * | Valid PR | Property w/ promotion and not promoted | Medium | Disabled |
 * | Valid PR | Property w/ promotion and not promoted | Old | Deleted |
 * | Valid PR | Property w/ promotion and promoted | Recent | Kept |
 * | Valid PR | Property w/ promotion and promoted | Medium | Kept |
 * | Valid PR | Property w/ promotion and promoted | Old | Kept |
 * | Missing PR | No property | Recent | Kept |
 * | Missing PR | No property | Medium | Disabled |
 * | Missing PR | No property | Old | Deleted |
 * | Missing PR | Property w/o promotion | Recent | Kept |
 * | Missing PR | Property w/o promotion | Medium | Disabled |
 * | Missing PR | Property w/o promotion | Old | Deleted |
 * | Missing PR | Property w/ promotion and not promoted | Recent | Kept |
 * | Missing PR | Property w/ promotion and not promoted | Medium | Disabled |
 * | Missing PR | Property w/ promotion and not promoted | Old | Deleted |
 * | Missing PR | Property w/ promotion and promoted | Recent | Kept |
 * | Missing PR | Property w/ promotion and promoted | Medium | Kept |
 * | Missing PR | Property w/ promotion and promoted | Old | Kept |
 * | Invalid PR | No property | Recent | Kept |
 * | Invalid PR | No property | Medium | Disabled |
 * | Invalid PR | No property | Old | Deleted |
 * | Invalid PR | Property w/o promotion | Recent | Kept |
 * | Invalid PR | Property w/o promotion | Medium | Disabled |
 * | Invalid PR | Property w/o promotion | Old | Deleted |
 * | Invalid PR | Property w/ promotion and not promoted | Recent | Kept |
 * | Invalid PR | Property w/ promotion and not promoted | Medium | Disabled |
 * | Invalid PR | Property w/ promotion and not promoted | Old | Deleted |
 * | Invalid PR | Property w/ promotion and promoted | Recent | Kept |
 * | Invalid PR | Property w/ promotion and promoted | Medium | Kept |
 * | Invalid PR | Property w/ promotion and promoted | Old | Kept |
 */
class PullRequestStaleBranchCompleteCheckIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitMockingConfigurator: GitMockingConfigurator

    @Autowired
    private lateinit var staleJobService: StaleJobService

    @Before
    fun init() {
        gitMockingConfigurator.clearPullRequests()
    }

    @Test
    fun `All tests`() {
        notAPR() withProperty none() withBuild recent() returns kept()
        notAPR() withProperty none() withBuild medium() returns kept()
        notAPR() withProperty none() withBuild old() returns kept()
        notAPR() withProperty propertyWithoutPromotion() withBuild recent() returns kept()
        notAPR() withProperty propertyWithoutPromotion() withBuild medium() returns disabled()
        notAPR() withProperty propertyWithoutPromotion() withBuild old() returns deleted()
        notAPR() withProperty propertyWithPromotionAndNotPromoted() withBuild recent() returns kept()
        notAPR() withProperty propertyWithPromotionAndNotPromoted() withBuild medium() returns disabled()
        notAPR() withProperty propertyWithPromotionAndNotPromoted() withBuild old() returns deleted()
        notAPR() withProperty propertyWithPromotionAndPromoted() withBuild recent() returns kept()
        notAPR() withProperty propertyWithPromotionAndPromoted() withBuild medium() returns kept()
        notAPR() withProperty propertyWithPromotionAndPromoted() withBuild old() returns kept()
    }

    private inner class ExpectedResult(
            val description: String,
            val checkFn: (description: String, Branch?) -> Unit
    ) {

        fun check(description: String, branch: Branch?) {
            checkFn(description, branch)
        }
    }

    private fun kept() = ExpectedResult("kept") { description, branch ->
        assertNotNull(branch, "$description: Branch is kept") {
            assertFalse(it.isDisabled, "$description: Branch is not disabled")
        }
    }

    private fun disabled() = ExpectedResult("disabled") { description, branch ->
        assertNotNull(branch, "$description: Branch is kept") {
            assertTrue(it.isDisabled, "$description: Branch is disabled")
        }
    }

    private fun deleted() = ExpectedResult("deleted") { description, branch ->
        assertNull(branch, "$description: Branch is deleted")
    }

    private inner class BranchAndBuildConfiguration(
            private val branchConfiguration: BranchConfiguration,
            private val buildConfiguration: BuildConfiguration
    ) {
        infix fun returns(expectedResult: ExpectedResult) {
            createRepo {
                commits(1)
            } and { repo, _ ->
                project {
                    prGitProject(repo)
                    // Stale property for project
                    branchConfiguration.configureProject(this)
                    // Branch
                    branch {
                        // Configuration of the branch
                        branchConfiguration.configureBranch(this)
                        // Build
                        build {
                            // Configures the build
                            branchConfiguration.configureBuild(this)
                            // Additional configuration
                            buildConfiguration.configure(this)
                            // Launches the staleness computation
                            staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                            // Expectations
                            val actualBranch = structureService.findBranchByID(branch.id)
                            val description = "${branchConfiguration.description} with ${buildConfiguration.description} expects to be ${expectedResult.description}"
                            expectedResult.check(description, actualBranch)
                        }
                    }
                }
            }
        }
    }

    private inner class BuildConfiguration(
            private val age: Long
    ) {

        val description: String = "build $age days old"

        fun configure(build: Build) {
            if (age > 0) {
                build.updateBuildSignature(time = Time.now().minusDays(age))
            }
        }
    }

    private fun recent() = BuildConfiguration(age = 0)
    private fun medium() = BuildConfiguration(age = 6)
    private fun old() = BuildConfiguration(age = 15)

    private inner class BranchConfiguration(
            private val branchPRConfiguration: BranchPRConfiguration,
            private val propertyConfiguration: PropertyConfiguration
    ) {
        infix fun withBuild(buildConfiguration: BuildConfiguration) =
                BranchAndBuildConfiguration(this, buildConfiguration)

        fun configureBranch(branch: Branch) {
            branchPRConfiguration.configure(branch)
            propertyConfiguration.configureBranch(branch)
        }

        fun configureProject(project: Project) {
            propertyConfiguration.configureProject(project)
        }

        fun configureBuild(build: Build) {
            propertyConfiguration.configureBuild(build)
        }

        val description: String = "${branchPRConfiguration.description} with ${propertyConfiguration.description}"
    }

    private inner class PropertyConfiguration(
            val description: String,
            private val projectConfiguration: (Project) -> Unit,
            private val branchConfiguration: (Branch) -> Unit,
            private val buildConfiguration: (Build) -> Unit
    ) {

        fun configureProject(project: Project) {
            projectConfiguration(project)
        }

        fun configureBranch(branch: Branch) {
            branchConfiguration(branch)
        }

        fun configureBuild(build: Build) {
            buildConfiguration(build)
        }
    }

    private fun none() = PropertyConfiguration(
            description = "no stale property",
            projectConfiguration = {},
            branchConfiguration = {},
            buildConfiguration = {}
    )

    private fun propertyWithoutPromotion() = PropertyConfiguration(
            description = "stale property",
            projectConfiguration = {
                setProperty(it, StalePropertyType::class.java, StaleProperty(
                        disablingDuration = 5,
                        deletingDuration = 10,
                        promotionsToKeep = null
                ))
            },
            branchConfiguration = {},
            buildConfiguration = {}
    )

    private val promotionName = "PL"

    private fun propertyWithPromotionAndNotPromoted() = PropertyConfiguration(
            description = "stale property with promotion protection and no build being promoted",
            projectConfiguration = {
                setProperty(it, StalePropertyType::class.java, StaleProperty(
                        disablingDuration = 5,
                        deletingDuration = 10,
                        promotionsToKeep = listOf(promotionName)
                ))
            },
            branchConfiguration = {
                it.promotionLevel(promotionName)
            },
            buildConfiguration = {}
    )

    private fun propertyWithPromotionAndPromoted() = PropertyConfiguration(
            description = "stale property with promotion protection and no build being promoted",
            projectConfiguration = {
                setProperty(it, StalePropertyType::class.java, StaleProperty(
                        disablingDuration = 5,
                        deletingDuration = 10,
                        promotionsToKeep = listOf(promotionName)
                ))
            },
            branchConfiguration = {
                it.promotionLevel(promotionName)
            },
            buildConfiguration = {
                val promotion = structureService.findPromotionLevelByName(it.project.name, it.branch.name, promotionName).getOrNull()
                        ?: error("Cannot find promotion level")
                it.promote(promotion)
            }
    )

    private inner class BranchPRConfiguration(
            val description: String,
            private val configuration: (Branch) -> Unit
    ) {

        infix fun withProperty(propertyConfiguration: PropertyConfiguration) =
                BranchConfiguration(this, propertyConfiguration)

        fun configure(branch: Branch) {
            configuration(branch)
        }

    }

    private fun notAPR() = BranchPRConfiguration("Branch which is not a PR") {
        gitMockingConfigurator.clearPullRequests()
        it.gitBranch("master") {
            commitAsProperty()
        }
    }

}