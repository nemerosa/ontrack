package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.SVNBuildResourceDecorationContributor
import net.nemerosa.ontrack.model.structure.ProjectEntityType
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
        assert !contributor.applyTo(ProjectEntityType.PROJECT)
    }

    @Test
    void 'No change log link on a branch'() {
        assert !contributor.applyTo(ProjectEntityType.BRANCH)
    }

}
