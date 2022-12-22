package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.git.exceptions.GitRepositoryNoRemoteException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Testing the behaviour of the projects when their Git remote cannot be reached.
 */
class GitServiceNoRemoteIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitNoRemoteCounter: GitNoRemoteCounter

    @Test
    fun `Disabling a project when number of attempts to sync exceeds a threshold`() {
        createRepo {
            // Creating a repository with some dummy content
            commits(1)
        } and { repo, _ ->
            project {
                // Using this repository
                gitProject(repo)
                // Gets its configuration
                val gitConfig = gitService.getProjectConfiguration(this)
                    ?: fail("We expect a Git configuration at project level.")
                // Synching the repository
                gitService.syncProjectRepository(gitConfig, this)
                // Checking that the counter has not been set at all
                assertEquals(0, gitNoRemoteCounter.getNoRemoteCount(name), "Git No Remote count has not been set")
                // Removing the remote (ie. the folder in this case)
                repo.close()
                // Resyncing (once) - checks that the counter has been incremented
                gitService.syncProjectRepository(gitConfig, this)
                assertEquals(1, gitNoRemoteCounter.getNoRemoteCount(name))
                // Tries until we run out of tries
                repeat(gitConfigProperties.remote.maxNoRemote) {
                    gitService.syncProjectRepository(gitConfig, this)
                }
                // Checks that the counter has increased
                assertTrue(gitNoRemoteCounter.getNoRemoteCount(name) > 1, "Git No Remote count has increased")
                // Checks that the project has been disabled
                assertTrue(structureService.getProject(id).isDisabled, "Project has been disabled")
            }
        }
    }

    @Test
    fun `Disabling a project does not happen when number of attempts to sync exceeds a threshold`() {
        val oldMaxNoRemote = gitConfigProperties.remote.maxNoRemote
        try {
            gitConfigProperties.remote.maxNoRemote = 0
            createRepo {
                // Creating a repository with some dummy content
                commits(1)
            } and { repo, _ ->
                project {
                    // Using this repository
                    gitProject(repo)
                    // Gets its configuration
                    val gitConfig = gitService.getProjectConfiguration(this)
                        ?: fail("We expect a Git configuration at project level.")
                    // Synching the repository
                    gitService.syncProjectRepository(gitConfig, this)
                    // Checking that the counter has not been set at all
                    assertEquals(0, gitNoRemoteCounter.getNoRemoteCount(name), "Git No Remote count has not been set")
                    // Removing the remote (ie. the folder in this case)
                    repo.close()
                    // Synching raises an error
                    assertFailsWith<GitRepositoryNoRemoteException> {
                        gitService.syncProjectRepository(gitConfig, this)
                    }
                }
            }
        } finally {
            gitConfigProperties.remote.maxNoRemote = oldMaxNoRemote
        }
    }

}