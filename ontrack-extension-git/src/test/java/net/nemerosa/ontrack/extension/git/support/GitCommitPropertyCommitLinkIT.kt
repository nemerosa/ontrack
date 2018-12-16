package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.service.GitService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class GitCommitPropertyCommitLinkIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitService: GitService

    @Test
    fun `Issue search on one branch`() {
        createRepo {
            commits(10)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    mapOf("1.0" to 3, "1.1" to 6).forEach { name, commitIndex ->
                        build(name) {
                            gitCommitProperty(commits.getOrFail(commitIndex))
                        }
                    }
                }
                // Gets commit info
                val commitInfo = asUserWithView(this).call {
                    gitService.getCommitProjectInfo(id, commits.getOrFail(4))
                }
                assertNotNull(commitInfo) {
                    assertEquals(
                            commits[4],
                            it.uiCommit.commit.id
                    )
                    assertEquals(
                            "Commit 4",
                            it.uiCommit.annotatedMessage
                    )
                    assertNotNull(it.firstBuild) { build ->
                        assertEquals(
                                "1.1",
                                build.name
                        )
                    }
                }
            }
        }
    }

}
