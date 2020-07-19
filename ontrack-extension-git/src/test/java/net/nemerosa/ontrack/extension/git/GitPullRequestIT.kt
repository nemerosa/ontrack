package net.nemerosa.ontrack.extension.git

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Interacting with pull requests
 */
class GitPullRequestIT : AbstractGitTestSupport() {

    @Before
    fun init() {
        // TODO Clears the mock service of all PRs
    }

    @Test
    fun `Branch configuration for a normal branch is not marked as pull request`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                branch {
                    gitBranch("release/1.0")
                    // Gets the Git configuration for this branch
                    val branchConfiguration = gitService.getBranchConfiguration(this)
                    assertNotNull(branchConfiguration) {
                        assertEquals("release/1.0", it.branch)
                        assertNull(it.pullRequest)
                    }
                }
            }
        }
    }

    @Test
    fun `Branch configuration for a PR is marked as pull request`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    // TODO Registers this PR in mock service
                    // Gets the Git configuration for this branch
                    val branchConfiguration = gitService.getBranchConfiguration(this)
                    assertNotNull(branchConfiguration) {
                        assertEquals("release/1.0", it.branch)
                        assertNotNull(it.pullRequest) { pr ->
                            assertEquals(1, pr.id)
                            assertEquals("PR-1", pr.key)
                            assertEquals("refs/heads/feature/TK-1-feature", pr.source)
                            assertEquals("refs/heads/release/1.0", pr.target)
                            assertEquals("Useful feature", pr.title)
                        }
                    }
                }
            }
        }
    }


}