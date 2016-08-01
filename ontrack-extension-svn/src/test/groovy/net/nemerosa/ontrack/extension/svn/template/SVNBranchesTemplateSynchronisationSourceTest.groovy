package net.nemerosa.ontrack.extension.svn.template

import net.nemerosa.ontrack.extension.svn.service.SVNService
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.tx.TransactionService
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class SVNBranchesTemplateSynchronisationSourceTest {

    SVNService svnService
    SVNBranchesTemplateSynchronisationSource source
    Branch branch

    @Before
    void before() {
        branch = Branch.of(Project.of(nd('P', "Project")), nd('B', "Branch"))
        svnService = mock(SVNService)
        TransactionService transactionService = mock(TransactionService)
        source = new SVNBranchesTemplateSynchronisationSource(
                svnService,
                transactionService
        )
    }

    @Test
    void 'Not applicable if branch not configured for Git'() {
        when(svnService.getSVNRepository(branch)).thenReturn(Optional.empty())
        assert !source.isApplicable(branch)
    }

    @Test
    void 'Applicable if branch configured for Git'() {
        when(svnService.getSVNRepository(branch)).thenReturn(Optional.of(SVNTestUtils.repository('svn://localhost')))
        assert source.isApplicable(branch)
    }

}