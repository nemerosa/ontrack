package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.scm.service.SCMServiceDetector
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

/**
 * Integration tests for Git as a SCM service
 */
class GitSCMServiceDetectorIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var scmServiceDetector: SCMServiceDetector

    @Test
    fun `Git as SCM service`() {
        withRepo { repo ->
            project {
                gitProject(repo)
                branch {
                    gitBranch(name) {
                        buildNameAsCommit()
                    }
                    // Gets the SCM service for this branch
                    asUserWithView(this).execute {
                        val oService = scmServiceDetector.getScmService(this)
                        assertPresent(oService) { service ->
                            val path = service.getSCMPathInfo(this)
                            assertPresent(path) {
                                assertEquals(
                                        "file://${repo.dir.absolutePath}",
                                        it.url
                                )
                                assertEquals(
                                        name,
                                        it.branch
                                )
                                assertEquals(
                                        "git",
                                        it.type
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
