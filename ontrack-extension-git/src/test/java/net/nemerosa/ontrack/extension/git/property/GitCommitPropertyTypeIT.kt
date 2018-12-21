package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNotNull

class GitCommitPropertyTypeIT : AbstractGitTestSupport() {

    @Test
    fun onPropertyChanged() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    build("1") {
                        gitCommitProperty(commits.getOrFail(1))
                        // Checks that we can now get a GitCommit for this build
                        val commit = gitService.getCommitForBuild(this)
                        assertNotNull(commit) {
                            assertEquals("Commit 1", it.shortMessage)
                        }
                    }
                }
            }
        }
    }
}