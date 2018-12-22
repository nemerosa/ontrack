package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class GitCommitPropertyCommitLinkIT : AbstractGitTestSupport() {

    @Test(expected = NoGitCommitPropertyException::class)
    fun `Commit from build without property`() {
        withRepo { repo ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    val link = gitService.getBranchConfiguration(this)!!.buildCommitLink!!
                    build("1") {
                        link.getCommitFromBuild(this)
                    }
                }
            }
        }
    }

    @Test
    fun `Commit from build with property`() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    val link = gitService.getBranchConfiguration(this)!!.buildCommitLink!!
                    build("1") {
                        gitCommitProperty(commits[1]!!)
                        val commit = link.getCommitFromBuild(this)
                        assertEquals(
                                commits[1],
                                commit
                        )
                    }
                }
            }
        }
    }

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
                }
            }
        }
    }

}
