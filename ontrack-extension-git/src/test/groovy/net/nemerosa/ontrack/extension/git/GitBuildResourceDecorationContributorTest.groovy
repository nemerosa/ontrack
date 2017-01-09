package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
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
        assert !contributor.applyTo(Project)
    }

    @Test
    void 'No change log link on a branch'() {
        assert !contributor.applyTo(Branch)
    }

}
