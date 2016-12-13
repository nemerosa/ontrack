package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.service.SCMUtilsService
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.tx.TransactionService
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GitServiceImplTest {

    private GitServiceImpl gitService
    private StructureService structureService
    private PropertyService propertyService

    @Before
    void 'Git service'() {
        structureService = mock(StructureService)

        propertyService = mock(PropertyService)

        def gitConfigurator = mock(GitConfigurator)
        when(gitConfigurator.getConfiguration(Mockito.any(Project.class))).thenReturn(
                Optional.of(
                        BasicGitConfiguration.empty()
                                .withRemote("remote")
                                .withName("MyGitConfig")
                )
        )

        gitService = new GitServiceImpl(
                structureService,
                propertyService
                ,
                mock(JobScheduler),
                mock(SecurityService),
                mock(TransactionService),
                mock(ApplicationLogService),
                mock(GitRepositoryClientFactory),
                mock(BuildGitCommitLinkService),
                [gitConfigurator],
                mock(SCMUtilsService)
        )
    }

    @Test
    void 'Looping over configured branches excludes branch templates'() {

        Project p1 = Project.of(nd('P1', "Project 1")).withId(ID.of(1))
        Project p2 = Project.of(nd('P2', "Project 2")).withId(ID.of(2))

        Branch b10 = Branch.of(p1, nd('B10', "Branch 1/0")).withId(ID.of(10)).withType(BranchType.TEMPLATE_DEFINITION)
        Branch b11 = Branch.of(p1, nd('B11', "Branch 1/1")).withId(ID.of(11)).withType(BranchType.TEMPLATE_INSTANCE)
        Branch b12 = Branch.of(p1, nd('B12', "Branch 1/2")).withId(ID.of(12)).withType(BranchType.TEMPLATE_INSTANCE)
        Branch b20 = Branch.of(p2, nd('B20', "Branch 2/0")).withId(ID.of(20))

        when(structureService.getProjectList()).thenReturn([p1, p2])
        when(structureService.getBranchesForProject(ID.of(1))).thenReturn([b10, b11, b12])
        when(structureService.getBranchesForProject(ID.of(2))).thenReturn([b20])


        def buildGitCommitLinkService = mock(BuildGitCommitLinkService)
        [b11, b12, b20].each { branch ->
            when(propertyService.getProperty(branch, GitBranchConfigurationPropertyType)).thenReturn(
                    new Property<GitBranchConfigurationProperty>(
                            new GitBranchConfigurationPropertyType(
                                    new GitExtensionFeature(new SCMExtensionFeature()),
                                    buildGitCommitLinkService,
                                    gitService
                            ),
                            null,
                            false
                    )
            )
        }

        List<String> branches = []
        gitService.forEachConfiguredBranch { branch, config -> branches.add(branch.name) }

        assert branches == ['B11', 'B12', 'B20']
    }

    @Test
    void 'Getting the earliest build after a commit'() {

        // Structure
        Project project = Project.of(nd('P1', "Project 1")).withId(ID.of(1))
        Branch branch = Branch.of(project, nd('B1', "Branch 1")).withId(ID.of(10))

        // Builds
        (1..15).each {
            when(structureService.findBuildByName('P1', 'B1', "1.0.${it}")).thenReturn(
                    Optional.of(
                            Build.of(
                                    branch,
                                    nd("1.0.${it}", "Build 1.0.${it}"),
                                    Signature.of('test')
                            ).withId(ID.of(it))
                    )
            )
        }

        // Git configuration
        BasicGitConfiguration gitConfiguration = BasicGitConfiguration.empty()
        GitBranchConfiguration branchConfiguration = GitBranchConfiguration.of(
                gitConfiguration,
                branch.name
        )

        // Git client
        GitRepositoryClient gitClient = mock(GitRepositoryClient)
        when(gitClient.getTagsWhichContainCommit('abcdef')).thenReturn(['1.0.12', '1.0.11', '1.0.10'])

        // Gets the earliest build
        def build = gitService.getEarliestBuildAfterCommit(
                'abcdef',
                branch,
                branchConfiguration,
                gitClient
        )

        // Checks
        assert build.present
        assert build.get().name == '1.0.10'

    }

}
