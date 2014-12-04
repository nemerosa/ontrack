package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.git.model.FormerGitConfiguration
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Matchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GitBranchesTemplateSynchronisationSourceTest {

    final GitExtensionFeature extensionFeature = new GitExtensionFeature()
    ExtensionManager extensionManager
    GitService gitService
    GitBranchesTemplateSynchronisationSource source
    FormerGitConfiguration gitConfiguration
    Branch branch

    @Before
    void before() {
        branch = Branch.of(Project.of(nd('P', "Project")), nd('B', "Branch"))
        gitConfiguration = FormerGitConfiguration.empty().withBranch('${sourceName}')
        extensionManager = mock(ExtensionManager)
        gitService = mock(GitService)
        when(gitService.getBranchConfiguration(branch)).thenReturn(gitConfiguration)
        when(gitService.getRemoteBranches(any(FormerGitConfiguration))).thenReturn(
                ['master', 'feature/ontrack-40-templating', 'feature/ontrack-111-project-manager', 'fix/ontrack-110']
        )
        source = new GitBranchesTemplateSynchronisationSource(
                extensionFeature,
                extensionManager,
                gitService
        )
    }

    @Test
    void 'Not applicable if feature not enabled'() {
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(false)
        when(gitService.isBranchConfiguredForGit(branch)).thenReturn(true)
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Not applicable if branch not configured for Git'() {
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(true)
        when(gitService.isBranchConfiguredForGit(branch)).thenReturn(false)
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Applicable if feature enabled and branch configured for Git'() {
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(true)
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