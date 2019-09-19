package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.model.structure.BranchModelMatcherService
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GitBranchModelMatcherProviderIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var branchModelMatcherService: BranchModelMatcherService

    @Test
    fun `List of project branches restricted to the default branching model for a Git project`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                gitProject(repo)
                val master = branch("master") { gitBranch("master") }
                val develop = branch("develop") { gitBranch("develop") }
                val release = branch("release-1.0") { gitBranch("release/1.0") }
                val feature = branch("feature-123-my-feature") { gitBranch("feature/123-my-feature") }
                // Gets the matcher for the project
                val matcher = branchModelMatcherService.getBranchModelMatcher(this)
                // Checks the branches
                assertNotNull(matcher) {
                    assertTrue(it.matches(master), "Master branch matching")
                    assertTrue(it.matches(develop), "Develop branch matching")
                    assertTrue(it.matches(release), "Release branch matching")
                    assertFalse(it.matches(feature), "Feature branch not matching")
                }
            }
        }
    }

    @Test
    fun `List of project branches restricted to a custom branching model for a Git project`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                gitProject(repo)
                setProperty(this, BranchingModelPropertyType::class.java,
                        BranchingModelProperty(listOf(
                                NameValue("Development", "master"),
                                NameValue("Release", "release/.*")
                        )
                        )
                )
                val master = branch("master") { gitBranch("master") }
                val develop = branch("develop") { gitBranch("develop") }
                val release = branch("release-1.0") { gitBranch("release/1.0") }
                val feature = branch("feature-123-my-feature") { gitBranch("feature/123-my-feature") }
                // Gets the matcher for the project
                val matcher = branchModelMatcherService.getBranchModelMatcher(this)
                // Checks the branches
                assertNotNull(matcher) {
                    assertTrue(it.matches(master), "Master branch matching")
                    assertFalse(it.matches(develop), "Develop branch not matching")
                    assertTrue(it.matches(release), "Release branch matching")
                    assertFalse(it.matches(feature), "Feature branch not matching")
                }
            }
        }
    }

    @Test
    fun `List of project branches unrestricted for a non-Git project`() {
        project {
            // Gets the matcher for the project
            val matcher = branchModelMatcherService.getBranchModelMatcher(this)
            // No matcher available
            assertNull(matcher, "No matcher available")
        }
    }

}