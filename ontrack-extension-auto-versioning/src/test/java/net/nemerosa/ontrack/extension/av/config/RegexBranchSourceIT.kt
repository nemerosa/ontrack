package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@AsAdminTest
class RegexBranchSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var regexBranchSource: RegexBranchSource

    @Test
    fun `Missing regular expression`() {
        project {
            branch {
                val dependency = this
                project {
                    branch {
                        val target = this
                        assertFailsWith<BranchSourceMissingConfigurationException> {
                            regexBranchSource.getLatestBranch(null, dependency.project, target, "ANY")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Regular expression`() {
        project {
            val dependencyProject = this
            /* val dependency11 = */ branch("release-1.11")
            val dependency12 = branch("release-1.12")
            /* val dependency20 = */ branch("release-1.20")

            project {
                branch {
                    val target = this

                    val latest = regexBranchSource.getLatestBranch("release-1\\.1.*", dependencyProject, target, "ANY")
                    assertEquals(dependency12, latest)
                }
            }
        }
    }

    @Test
    fun `Regular expression not taking the disabled branches into account`() {
        project {
            val dependencyProject = this
            val dependency11 = branch("release-1.11")
            /* val dependency12 = */ branch("release-1.12") {
                structureService.disableBranch(this)
            }
            /* val dependency20 = */ branch("release-1.20")

            project {
                branch {
                    val target = this

                    val latest = regexBranchSource.getLatestBranch("release-1\\.1.*", dependencyProject, target, "ANY")
                    assertEquals(dependency11, latest)
                }
            }
        }

    }

}