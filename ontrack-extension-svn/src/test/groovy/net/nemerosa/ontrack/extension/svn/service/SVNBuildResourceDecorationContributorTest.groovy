package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.SVNBuildResourceDecorationContributor
import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.ui.resource.LinksBuilder
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

class SVNBuildResourceDecorationContributorTest {

    private SVNService svnService
    private SVNBuildResourceDecorationContributor contributor

    @Before
    void 'Setup'() {
        svnService = mock(SVNService)
        contributor = new SVNBuildResourceDecorationContributor(svnService)
    }

    @Test
    void 'No change log link on a project'() {
        Project project = Project.of(nd('P', ''))
        LinksBuilder linksBuilder = mock(LinksBuilder)
        contributor.contribute(linksBuilder, project)
        verifyZeroInteractions(linksBuilder)
    }

    @Test
    void 'No change log link on a branch'() {
        Branch branch = Branch.of(
                Project.of(nd('P', '')),
                nd('B', '')
        )
        LinksBuilder linksBuilder = mock(LinksBuilder)
        contributor.contribute(linksBuilder, branch)
        verifyZeroInteractions(linksBuilder)
    }

    @Test
    void 'No change log link on a build not configured'() {
        Branch branch = Branch.of(
                Project.of(nd('P', '')),
                nd('B', '')
        )
        Build build = Build.of(branch, nd('1', ''), Signature.of('test'))

        when(svnService.getSVNRepository(branch)).thenReturn(Optional.empty())

        LinksBuilder linksBuilder = mock(LinksBuilder)

        contributor.contribute(linksBuilder, build)

        verifyZeroInteractions(linksBuilder)
    }

    @Test
    void 'Change log link on a build'() {
        Branch branch = Branch.of(
                Project.of(nd('P', '')),
                nd('B', '')
        )
        Build build = Build.of(branch, nd('1', ''), Signature.of('test'))

        when(svnService.getSVNRepository(branch)).thenReturn(Optional.of(
                mock(SVNRepository)
        ))

        LinksBuilder linksBuilder = mock(LinksBuilder)

        contributor.contribute(linksBuilder, build)

        verify(linksBuilder).link(
                eq('_changeLog'),
                any(),
                eq(ProjectView),
                eq(build)
        )
    }
}
