package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.BasicGitActualConfiguration
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GitBranchesTemplateSynchronisationSourceTest {

    GitService gitService
    GitBranchesTemplateSynchronisationSource source
    GitConfiguration gitConfiguration
    Branch branch
    Project project

    @Before
    void before() {
        project = Project.of(nd('P', "Project"))
        branch = Branch.of(project, nd('B', "Branch"))
        gitConfiguration = BasicGitActualConfiguration.of(BasicGitConfiguration.empty())
        gitService = mock(GitService)
        when(gitService.getProjectConfiguration(project)).thenReturn(Optional.of(gitConfiguration))
        when(gitService.getRemoteBranches(gitConfiguration)).thenReturn(
                ['master', 'feature/ontrack-40-templating', 'feature/ontrack-111-project-manager', 'fix/ontrack-110']
        )
        source = new GitBranchesTemplateSynchronisationSource(
                gitService
        )
    }

    @Test
    void 'Not applicable if branch not configured for Git'() {
        when(gitService.isBranchConfiguredForGit(branch)).thenReturn(false)
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Applicable if branch configured for Git'() {
        when(gitService.isBranchConfiguredForGit(branch)).thenReturn(true)
        assert source.isApplicable(branch)
    }

    @Test
    void 'Branches - no filter'() {
        assert source.getBranchNames(branch, new GitBranchesTemplateSynchronisationSourceConfig(
                "",
                ""
        )) == ['feature/ontrack-111-project-manager', 'feature/ontrack-40-templating', 'fix/ontrack-110', 'master']
    }

    @Test
    void 'Branches - includes all'() {
        assert source.getBranchNames(branch, new GitBranchesTemplateSynchronisationSourceConfig(
                "*",
                ""
        )) == ['feature/ontrack-111-project-manager', 'feature/ontrack-40-templating', 'fix/ontrack-110', 'master']
    }

    @Test
    void 'Branches - exclude master'() {
        assert source.getBranchNames(branch, new GitBranchesTemplateSynchronisationSourceConfig(
                "",
                "master"
        )) == ['feature/ontrack-111-project-manager', 'feature/ontrack-40-templating', 'fix/ontrack-110']
    }

    @Test
    void 'Branches - include only'() {
        assert source.getBranchNames(branch, new GitBranchesTemplateSynchronisationSourceConfig(
                "fix/*",
                ""
        )) == ['fix/ontrack-110']
    }

    @Test
    void 'Branches - include/exclude'() {
        assert source.getBranchNames(branch, new GitBranchesTemplateSynchronisationSourceConfig(
                "feature/*",
                "*templating"
        )) == ['feature/ontrack-111-project-manager']
    }

}