package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GitBranchesTemplateSynchronisationSourceIT {

    final GitExtensionFeature extensionFeature = new GitExtensionFeature()
    ExtensionManager extensionManager
    GitService gitService
    GitBranchesTemplateSynchronisationSource source

    @Before
    void before() {
        extensionManager = mock(ExtensionManager)
        gitService = mock(GitService)
        source = new GitBranchesTemplateSynchronisationSource(
                extensionFeature,
                extensionManager,
                gitService
        )
    }

    @Test
    void 'Not applicable if feature not enabled'() {
        Branch branch = Branch.of(Project.of(nd('P', "Project")), nd('B', "Branch"))
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(false)
        when(gitService.isBranchConfiguredForGit(branch)).thenReturn(true)
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Not applicable if branch not configured for Git'() {
        Branch branch = Branch.of(Project.of(nd('P', "Project")), nd('B', "Branch"))
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(true)
        when(gitService.isBranchConfiguredForGit(branch)).thenReturn(false)
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Applicable if feature enabled and branch configured for Git'() {
        Branch branch = Branch.of(Project.of(nd('P', "Project")), nd('B', "Branch"))
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(true)
        when(gitService.isBranchConfiguredForGit(branch)).thenReturn(true)
        assert source.isApplicable(branch)
    }

}