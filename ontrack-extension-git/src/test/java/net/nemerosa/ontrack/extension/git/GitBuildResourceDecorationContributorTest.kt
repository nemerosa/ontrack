package net.nemerosa.ontrack.extension.git

import io.mockk.mockk
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class GitBuildResourceDecorationContributorTest {

    private lateinit var contributor: GitBuildResourceDecorationContributor

    @BeforeEach
    fun setup() {
        contributor = GitBuildResourceDecorationContributor(mockk<GitService>())
    }

    @Test
    fun `No change log link on a project`() {
        assertFalse(contributor.applyTo(ProjectEntityType.PROJECT))
    }

    @Test
    fun `No change log link on a branch`() {
        assertFalse(contributor.applyTo(ProjectEntityType.BRANCH))
    }

}
