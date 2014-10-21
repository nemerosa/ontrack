package net.nemerosa.ontrack.extension.svn.template

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature
import net.nemerosa.ontrack.extension.svn.service.SVNService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class SVNBranchesTemplateSynchronisationSourceTest {

    final SVNExtensionFeature extensionFeature = new SVNExtensionFeature()
    ExtensionManager extensionManager
    SVNService svnService
    SVNBranchesTemplateSynchronisationSource source
    Branch branch

    @Before
    void before() {
        branch = Branch.of(Project.of(nd('P', "Project")), nd('B', "Branch"))
        extensionManager = mock(ExtensionManager)
        svnService = mock(SVNService)
        source = new SVNBranchesTemplateSynchronisationSource(
                extensionFeature,
                extensionManager,
                svnService
        )
    }

    @Test
    void 'Not applicable if feature not enabled'() {
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(false)
        when(svnService.getSVNRepository(branch)).thenReturn(Optional.of(SVNTestUtils.repository()))
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Not applicable if branch not configured for Git'() {
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(true)
        when(svnService.getSVNRepository(branch)).thenReturn(Optional.empty())
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Applicable if feature enabled and branch configured for Git'() {
        when(extensionManager.isExtensionFeatureEnabled(extensionFeature)).thenReturn(true)
        when(svnService.getSVNRepository(branch)).thenReturn(Optional.of(SVNTestUtils.repository()))
        assert source.isApplicable(branch)
    }

}