package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.api.model.BuildValidationException
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.Test
import org.springframework.test.context.ActiveProfiles

/**
 * Tests for #187 - validation of the build name
 */
@ActiveProfiles(profiles = [RunProfile.UNIT_TEST, "git.mock"])
class GitBuildValidationIT : AbstractGitTestSupport() {

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test
    fun `Build validation OK without branch config`() {
        project {
            branch {
                build()
            }
        }
    }

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test
    fun `Build validation OK with tag pattern`() {
        withRepo { repo ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        tagPatternBuildName("1.1.*")
                    }
                    // Build with correct name
                    build("1.1.0")
                }
            }
        }
    }

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test(expected = BuildValidationException::class)
    fun `Build validation not OK with tag pattern`() {
        withRepo { repo ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        tagPatternBuildName("1.1.*")
                    }
                    // Build with incorrect name
                    build("1.2.0")
                }
            }
        }
    }

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test(expected = BuildValidationException::class)
    fun `Build validation not OK on rename`() {
        withRepo { repo ->
            project {
                gitProject(repo)
                branch branch@{
                    gitBranch {
                        tagPatternBuildName("1.1.*")
                    }
                    // Build with correct name
                    build("1.1.0") build@{
                        // Renames the build
                        asUser().with(this@branch, ProjectEdit::class.java).execute {
                            structureService.saveBuild(
                                    Build.of(
                                            branch,
                                            nd("1.2.0", "New build"),
                                            securityService.currentSignature
                                    ).withId(this@build.id)
                            )
                        }
                    }
                }
            }
        }
    }
}
