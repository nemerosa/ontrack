package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.mocking.GitMockingConfigurator
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Interacting with pull requests
 */
class GitPullRequestIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitMockingConfigurator: GitMockingConfigurator

    @Before
    fun init() {
        gitMockingConfigurator.clearPullRequests()
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
            gitMockingConfigurator.registerPullRequest(1, title = "Useful feature")
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    // Registers this PR in mock service
                    // Gets the Git configuration for this branch
                    val branchConfiguration = gitService.getBranchConfiguration(this)
                    assertNotNull(branchConfiguration) {
                        assertEquals("PR-1", it.branch)
                        assertNotNull(it.pullRequest) { pr ->
                            assertEquals(1, pr.id)
                            assertEquals(true, pr.isValid)
                            assertEquals("#1", pr.key)
                            assertEquals("feature/TK-1-feature", pr.source)
                            assertEquals("release/1.0", pr.target)
                            assertEquals("Useful feature", pr.title)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `No PR information when not enabled`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            gitMockingConfigurator.registerPullRequest(1, title = "Useful feature")
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    // Disabling pull requests
                    gitConfigProperties.pullRequests.enabled = false
                    try {
                        // Gets the Git configuration for this branch
                        val branchConfiguration = gitService.getBranchConfiguration(this)
                        assertNotNull(branchConfiguration) {
                            assertEquals("PR-1", it.branch)
                            assertNull(it.pullRequest, "PR is not reported since it's deactivated")
                        }
                    } finally {
                        gitConfigProperties.pullRequests.enabled = true
                    }
                }
            }
        }
    }

    @Test
    fun `PR status changing`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            gitMockingConfigurator.registerPullRequest(1, status = "open")
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    // Checks the status
                    assertEquals(
                            "open",
                            gitService.getBranchConfiguration(this)
                                    ?.pullRequest
                                    ?.status
                    )
                    // Changing the status
                    withPRCacheDisabled {
                        gitMockingConfigurator.registerPullRequest(1, status = "closed")
                        // Getting the new status
                        assertEquals(
                                "closed",
                                gitService.getBranchConfiguration(this)
                                        ?.pullRequest
                                        ?.status
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `PR information as the PR is deleted`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            // Registers a PR
            gitMockingConfigurator.registerPullRequest(1)
            project {
                prGitProject(repo)
                branch {
                    // Creates a branch for this PR
                    gitBranch("PR-1")
                    // Checks that the PR is associated to the branch
                    assertNotNull(gitService.getBranchConfiguration(this)) { config ->
                        assertNotNull(config.pullRequest) { pr ->
                            assertEquals(1, pr.id)
                            assertEquals(true, pr.isValid)
                            assertEquals("#1", pr.key)
                            assertEquals("feature/TK-1-feature", pr.source)
                            assertEquals("release/1.0", pr.target)
                            assertEquals("PR n°1", pr.title)
                        }
                    }
                    withPRCacheDisabled {
                        // Removes the PR (deletion)
                        gitMockingConfigurator.unregisterPullRequest(1)
                        // Checks that the PR is associated to the branch
                        assertNotNull(gitService.getBranchConfiguration(this)) { config ->
                            assertNotNull(config.pullRequest, "Branch still identified as a pull request") { pr ->
                                assertEquals(1, pr.id)
                                assertEquals(false, pr.isValid)
                                assertEquals("#1", pr.key)
                                assertEquals("", pr.source)
                                assertEquals("", pr.target)
                                assertEquals("", pr.title)
                            }
                        }
                    }
                }
            }
        }
    }


}