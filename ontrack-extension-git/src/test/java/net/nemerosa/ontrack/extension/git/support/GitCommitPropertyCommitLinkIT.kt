package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class GitCommitPropertyCommitLinkIT : AbstractGitTestSupport() {

    @Test
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
                        assertFailsWith<NoGitCommitPropertyException> {
                            link.getCommitFromBuild(this)
                        }
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

}
