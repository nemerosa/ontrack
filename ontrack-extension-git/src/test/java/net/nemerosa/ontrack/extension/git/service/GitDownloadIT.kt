package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

/**
 * Integration tests for downloading a file
 */
class GitDownloadIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitService: GitService

    @Test
    fun `Download files`() {
        createRepo {
            // Creates a Git repository with 10 commits and a branch
            file("folder/file1", "Content1")
            git("commit", "-m", "Commit 1")
            git("checkout", "-b", "branch1")
            git("checkout", "master")
            file("folder/file1", "Content2")
            git("commit", "-m", "Commit 2")
            // Identifies the commits
            (1..2)
                    .associate {
                        it to commitLookup("Commit $it")
                    }
        } and { repo, commit ->
            // Creates a project and branches
            project {
                gitProject(repo)
                branch("branch1") {
                    gitBranch("branch1") {
                        buildNameAsCommit()
                    }
                    // Downloads the files for this branch
                    asUser().with(project, ProjectConfig::class.java).call {
                        assertPresent(gitService.download(this, "folder/file1")) {
                            assertEquals("Content1", it)
                        }
                    }
                }
                branch("master") {
                    gitBranch("master") {
                        buildNameAsCommit()
                    }
                    // Downloads the files for this branch
                    asUser().with(project, ProjectConfig::class.java).call {
                        assertPresent(gitService.download(this, "folder/file1")) {
                            assertEquals("Content2", it)
                        }
                    }
                }
            }
        }
    }

}
