package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.AbstractGitSearchTestSupport
import net.nemerosa.ontrack.extension.git.GitCommitSearchExtension
import net.nemerosa.ontrack.extension.git.GitIssueSearchExtension
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitIssueInfoGraphQLIT : AbstractGitSearchTestSupport() {

    @Autowired
    private lateinit var gitIssueSearchExtension: GitIssueSearchExtension

    @Autowired
    private lateinit var gitCommitSearchExtension: GitCommitSearchExtension

    @Test
    fun `Getting issue info`() {
        createRepo {
            listOf(
                    "No issue",
                    "Issue #1",
                    "Issue #2 and #3",
                    "Issue #1 again"
            ).mapIndexed { no, message ->
                (no + 1) to commit(no + 1, message, true)
            }.toMap()
        } and { repo, commits ->
            project {
                gitProject(repo)
                // Setup
                branch("master") {
                    gitBranch("master") {
                        commitAsProperty()
                    }
                    // Creates some builds on this branch, for some commits only
                    build(1, commits)
                    build(2, commits)
                    build(3, commits)
                    build(4, commits)
                    // Indexation of issues (this goes through commit indexation)
                    searchIndexService.index(gitCommitSearchExtension)
                    // Looks for issue 2
                    val data = asUserWithView {
                        run("""
                    query IssueInfo(${'$'}issue: String!) {
                        gitIssueInfo(issue: ${'$'}issue) {
                            commitInfo {
                                uiCommit {
                                  annotatedMessage
                                }
                                branchInfosList {
                                  branchInfoList {
                                    firstBuild {
                                      name
                                    }
                                  }
                                }
                            }
                        }
                    }
                """, mapOf("issue" to "#2"))
                    }
                    val commitInfo = data.path("gitIssueInfo").path("commitInfo")
                    assertEquals("""Issue <a href="http://issue/2">#2</a> and <a href="http://issue/3">#3</a>""", commitInfo.path("uiCommit").path("annotatedMessage").asText())
                    assertEquals("3", commitInfo.path("branchInfosList").first()
                            .path("branchInfoList").first()
                            .path("firstBuild").path("name").asText()
                    )
                }
            }
        }
    }

}