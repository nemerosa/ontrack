package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock

class GitBuildResourceDecorationContributorTest {

    private GitBuildResourceDecorationContributor contributor

    @Before
    void 'Setup'() {
        contributor = new GitBuildResourceDecorationContributor(mock(GitService))
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
