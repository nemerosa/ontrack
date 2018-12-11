package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.model.BasicGitActualConfiguration
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.repository.GitRepositoryHelper
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.service.SCMUtilsService
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.tx.TransactionService
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import java.util.function.BiConsumer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitServiceImplTest {

    private lateinit var gitService: GitServiceImpl
    private lateinit var structureService: StructureService
    private lateinit var propertyService: PropertyService

    @Before
    fun `Git service`() {
        structureService = mock(StructureService::class.java)

        propertyService = mock(PropertyService::class.java)

        val gitConfigurator = mock(GitConfigurator::class.java)
        `when`(gitConfigurator.getConfiguration(Mockito.any(Project::class.java))).thenReturn(
                Optional.of(
                        BasicGitActualConfiguration.of(
                                BasicGitConfiguration.empty()
                                        .withRemote("remote")
                                        .withName("MyGitConfig")
                        )
                )
        )

        gitService = GitServiceImpl(
                structureService,
                propertyService,
                mock(JobScheduler::class.java),
                mock(SecurityService::class.java),
                mock(TransactionService::class.java),
                mock(ApplicationLogService::class.java),
                mock(GitRepositoryClientFactory::class.java),
                mock(BuildGitCommitLinkService::class.java),
                listOf(gitConfigurator),
                mock(SCMUtilsService::class.java),
                mock(GitRepositoryHelper::class.java)
        )
    }

    @Test
    fun `Looping over configured branches excludes branch templates`() {

        val p1 = Project.of(nd("P1", "Project 1")).withId(ID.of(1))
        val p2 = Project.of(nd("P2", "Project 2")).withId(ID.of(2))

        val b10 = Branch.of(p1, nd("B10", "Branch 1/0")).withId(ID.of(10)).withType(BranchType.TEMPLATE_DEFINITION)
        val b11 = Branch.of(p1, nd("B11", "Branch 1/1")).withId(ID.of(11)).withType(BranchType.TEMPLATE_INSTANCE)
        val b12 = Branch.of(p1, nd("B12", "Branch 1/2")).withId(ID.of(12)).withType(BranchType.TEMPLATE_INSTANCE)
        val b20 = Branch.of(p2, nd("B20", "Branch 2/0")).withId(ID.of(20))

        `when`(structureService.projectList).thenReturn(listOf(p1, p2))
        `when`(structureService.getBranchesForProject(ID.of(1))).thenReturn(listOf(b10, b11, b12))
        `when`(structureService.getBranchesForProject(ID.of(2))).thenReturn(listOf(b20))


        val buildGitCommitLinkService = mock(BuildGitCommitLinkService::class.java)
        listOf(b11, b12, b20).forEach { branch ->
            `when`(propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java)).thenReturn(
                    Property.of(
                            GitBranchConfigurationPropertyType(
                                    GitExtensionFeature(SCMExtensionFeature()),
                                    buildGitCommitLinkService,
                                    gitService
                            ),
                            null
                    )
            )
        }

        val branches = mutableListOf<String>()
        gitService.forEachConfiguredBranch(BiConsumer { branch, _ -> branches.add(branch.name) })

        assertEquals(
                listOf("B11", "B12", "B20"),
                branches
        )
    }

    @Test
    fun `Getting the earliest build after a commit`() {

        // Structure
        val project = Project.of(nd("P1", "Project 1")).withId(ID.of(1))
        val branch = Branch.of(project, nd("B1", "Branch 1")).withId(ID.of(10))

        // Builds
        (1..15).forEach {
            `when`(structureService.findBuildByName("P1", "B1", "1.0.$it")).thenReturn(
                    Optional.of(
                            Build.of(
                                    branch,
                                    nd("1.0.$it", "Build 1.0.$it"),
                                    Signature.of("test")
                            ).withId(ID.of(it))
                    )
            )
        }

        // Git configuration
        val gitConfiguration = BasicGitConfiguration.empty()
        val branchConfiguration = GitBranchConfiguration(
                BasicGitActualConfiguration.of(gitConfiguration),
                branch.name
        )

        // Git client
        val gitClient = mock(GitRepositoryClient::class.java)
        `when`(gitClient.getTagsWhichContainCommit("abcdef")).thenReturn(listOf("1.0.12", "1.0.11", "1.0.10"))

        // Gets the earliest build
        val build = gitService.getEarliestBuildAfterCommit(
                "abcdef",
                branch,
                branchConfiguration,
                gitClient
        )

        // Checks
        assertNotNull(build) {
            assertEquals("1.0.10", it.name)
        }

    }

}
