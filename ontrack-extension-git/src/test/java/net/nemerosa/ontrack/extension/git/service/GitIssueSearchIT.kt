package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.issues.support.MockIssue
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceExtension
import net.nemerosa.ontrack.extension.issues.support.MockIssueStatus
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitIssueSearchIT : AbstractGitTestSupport() {

    @Test
    fun `Issue search on one branch`() {
        createRepo {
            commit(1, "#1 First commit", pause = true)
            commit(2, "#2 Second commit", pause = true)
            commit(3, "#2 Third commit", pause = true)
            commit(4, "#1 Fourth commit")
        } and { repo, _ ->
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch {
                        buildNameAsCommit(abbreviated = true)
                    }
                    build(repo.commitLookup("#1 First commit"))
                    build(repo.commitLookup("#2 Third commit"))
                    build(repo.commitLookup("#1 Fourth commit"))
                    // Makes sure to index the branch builds commits
                    gitService.collectIndexableGitCommitForBranch(this)
                }
                // Looks for an issue
                val info = asUserWithView(this).call {
                    gitService.getIssueProjectInfo(id, "#2")
                }
                assertNotNull(info) { gitIssueInfo ->
                    assertEquals("2", gitIssueInfo.issue.key)
                    assertEquals("#2", gitIssueInfo.issue.displayKey)
                    assertNotNull(gitIssueInfo.commitInfo) { gitCommitInfo ->
                        gitCommitInfo.assertBranchInfos(
                                "Development" to listOf(
                                        BranchInfoTest(
                                                branch = "master",
                                                firstBuild = repo.commitLookup("#2 Third commit")
                                        )
                                )
                        )
                    }
                }
            }
        }
    }

    /**
     * Gets issue info across two branches.
     *
     * ```
     *     |  |
     *     *  | Commit #2 (master)
     *     |  |
     *     |  * Commit #2 (release/1.0)
     *     |  |
     *     |  /
     *     | /
     *     * Commit #2
     * ```
     *
     * Last commit for issue #2 is located on branch `master` and therefore
     * does not have any branch info (because not on a release branch).
     */
    @Test
    fun `Issue search between two branches`() {
        createRepo {
            val commit1 = commit(1, "Commit #2 one", pause = true)
            git("checkout", "-b", "1.0")
            val commit2 = commit(2, "Commit #2 two", pause = true)
            git("checkout", "master")
            val commit3 = commit(3, "Commit #2 three", pause = true)
            // Commits index
            mapOf(
                    1 to commit1,
                    2 to commit2,
                    3 to commit3
            )
        } and { repo, commits ->
            // Creates a project and two branches<
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch("master") {
                        commitAsProperty()
                    }
                    build("1") {
                        gitCommitProperty(commits.getValue(1))
                    }
                    build("3") {
                        gitCommitProperty(commits.getValue(3))
                    }
                }
                branch("release-1.0") {
                    gitBranch("release/1.0") {
                        commitAsProperty()
                    }
                    build("2") {
                        gitCommitProperty(commits.getValue(2))
                    }
                }

                // Issue service
                val issue2 = MockIssue(2, MockIssueStatus.OPEN, "feature")
                mockIssueServiceExtension.register(issue2)

                // Issue info
                val info = asUserWithView(this).call {
                    gitService.getIssueProjectInfo(id, "#2")
                }
                assertNotNull(info) { gitIssueInfo ->
                    assertEquals("2", gitIssueInfo.issue.key)
                    assertEquals("#2", gitIssueInfo.issue.displayKey)
                    assertNotNull(gitIssueInfo.commitInfo) { gitCommitInfo ->
                        gitCommitInfo.assertBranchInfos(
                                "Development" to listOf(
                                        BranchInfoTest(
                                                branch = "master",
                                                firstBuild = "3"
                                        )
                                )
                        )
                    }
                }
            }

        }
    }

}
