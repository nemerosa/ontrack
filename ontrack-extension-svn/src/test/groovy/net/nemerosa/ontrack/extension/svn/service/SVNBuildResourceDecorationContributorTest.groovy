package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.SVNBuildResourceDecorationContributor
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock

class SVNBuildResourceDecorationContributorTest {

    private SVNBuildResourceDecorationContributor contributor

    @Before
    void 'Setup'() {
        contributor = new SVNBuildResourceDecorationContributor(mock(SVNService))
    }

    @Test
    void 'No change log link on a project'() {
        assert !contributor.applyTo(Project)
    }

    @Test
    void 'No change log link on a branch'() {
        assert !contributor.applyTo(Branch)
    }

}
