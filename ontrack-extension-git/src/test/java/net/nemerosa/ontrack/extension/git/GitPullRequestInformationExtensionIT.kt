package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.mocking.GitMockingConfigurator
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.test.assertIs
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GitPullRequestInformationExtensionIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitMockingConfigurator: GitMockingConfigurator

    @Autowired
    private lateinit var entityInformationExtension: GitPullRequestInformationExtension

    @Before
    fun init() {
        gitMockingConfigurator.clearPullRequests()
    }

    @Test
    fun `No PR entity information for a normal branch`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                branch {
                    gitBranch("release/1.0")
                    // Gets the PR entity information
                    val info = entityInformationExtension.getInformation(this)
                    assertNull(info, "No extra PR entity information")
                }
            }
        }
    }

    @Test
    fun `PR entity information for a PR branch`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            // Registers this PR in mock service
            gitMockingConfigurator.registerPullRequest(1, title = "Useful feature")
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    // Gets the PR entity information
                    val info = entityInformationExtension.getInformation(this)
                    assertNotNull(info) {
                        assertIs<GitPullRequest>(it.data) { pr ->
                            assertEquals(1, pr.id)
                            assertEquals("#1", pr.key)
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