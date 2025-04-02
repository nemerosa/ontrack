package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.model.security.ProjectConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Integration tests for downloading a file
 */
class GitDownloadIT : AbstractGitTestSupport() {

    @Test
    fun `Download files`() {
        createRepo {
            // Creates a Git repository with 10 commits and a branch
            file("folder/file1", "Content1")
            git("commit", "-m", "Commit 1")
            git("checkout", "-b", "branch1")
            git("checkout", "main")
            file("folder/file1", "Content2")
            git("commit", "-m", "Commit 2")
            // Identifies the commits
            (1..2)
                    .associate {
                        it to commitLookup("Commit $it")
                    }
        } and { repo, _ ->
            // Creates a project and branches
            project {
                gitProject(repo)
                branch("branch1") {
                    gitBranch("branch1") {
                        buildNameAsCommit()
                    }
                    // Downloads the files for this branch
                    asUser().withProjectFunction(project, ProjectConfig::class.java).call {
                        assertEquals("Content1", gitService.download(project, "branch1", "folder/file1"))
                    }
                }
                branch("main") {
                    gitBranch("main") {
                        buildNameAsCommit()
                    }
                    // Downloads the files for this branch
                    asUser().withProjectFunction(project, ProjectConfig::class.java).call {
                        assertEquals("Content2", gitService.download(project, "main", "folder/file1"))
                    }
                }
            }
        }
    }

}
