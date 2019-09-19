package net.nemerosa.ontrack.extension.git.repository

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GitRepositoryHelperIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitRepositoryHelper: GitRepositoryHelper

    @Test
    fun `Getting a branch from its Git branch`() {
        // Creates a project with two branches
        // One configured for Git
        // The other not
        project {
            // Branch for Git
            val configuredBranch = branch {
                gitBranch("master")
            }
            // Branch not configured for Git
            val unconfiguredBranch = branch {}
            // Looking for the "master" branch should return the configured branch
            assertNotNull(gitRepositoryHelper.findBranchWithProjectAndGitBranch(this, "master")) {
                assertEquals(configuredBranch.id(), it)
            }
            // Looking for any other branch should not return anything
            assertNull(gitRepositoryHelper.findBranchWithProjectAndGitBranch(this, "release/1.0"))
        }
    }

}