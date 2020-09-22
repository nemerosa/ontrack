package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.mocking.GitMockingConfigurator
import net.nemerosa.ontrack.json.isNullOrNullNode
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitPullRequestGraphQLIT: AbstractGitTestSupport() {

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
                    // Gets the PR for this branch
                    val data = run("""{
                            branches(id: $id) {
                                pullRequest {
                                    id
                                    key
                                    isValid
                                    source
                                    target
                                    title
                                    status
                                    url
                                }
                            }
                        }""")
                    val pr = data["branches"][0].path("pullRequest")
                    assertTrue(pr.isNullOrNullNode(), "No pull request")
                }
            }
        }
    }

    @Test
    fun `Branch configuration for a PR is marked as pull request`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            gitMockingConfigurator.registerPullRequest(1)
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    // Gets the PR for this branch
                    val data = run("""{
                            branches(id: $id) {
                                pullRequest {
                                    id
                                    key
                                    isValid
                                    source
                                    target
                                    title
                                    status
                                    url
                                }
                            }
                        }""")
                    val pr = data["branches"][0].path("pullRequest")
                    assertEquals(1, pr.path("id").asInt())
                    assertEquals("#1", pr.path("key").asText())
                    assertEquals(true, pr.path("isValid").asBoolean())
                    assertEquals("feature/TK-1-feature", pr.path("source").asText())
                    assertEquals("release/1.0", pr.path("target").asText())
                    assertEquals("PR n°1", pr.path("title").asText())
                    assertEquals("open", pr.path("status").asText())
                    assertEquals("uri:testing:web:git:pr:1", pr.path("url").asText())
                }
            }
        }
    }
}