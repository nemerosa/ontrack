package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SameReleaseBranchSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var sameReleaseBranchSource: SameReleaseBranchSource

    @Test
    fun levels() {
        project {
            val dependencyProject = this
            /* val dependency2410 = */ branch("release/1.24.10")
            val dependency2411 = branch("release/1.24.11")
            val dependency2500 = branch("release/1.25.0")
            /* val dependencyXXXX = */ branch("release/2.0.0")
            project {
                branch("release/1.24.6") {
                    val target = this
                    assertEquals(
                        dependency2411,
                        sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target, "ANY")
                    )
                    assertEquals(
                        dependency2500,
                        sameReleaseBranchSource.getLatestBranch("1", dependencyProject, target, "ANY")
                    )
                    assertEquals(
                        dependency2500,
                        sameReleaseBranchSource.getLatestBranch("", dependencyProject, target, "ANY")
                    )
                    assertEquals(
                        dependency2500,
                        sameReleaseBranchSource.getLatestBranch("", dependencyProject, target, "ANY")
                    )
                }
            }
        }
    }

    @Test
    fun `Old versions must not be targeted by newer versions`() {
        project {
            val dependencyProject = this
            /* val dependency1250 = */ branch("release/1.25.0")
            val dependency1251 = branch("release/1.25.1")
            val dependency1260 = branch("release/1.26.0")
            project {
                val target1250 = branch("release/1.25.0")
                val target1251 = branch("release/1.25.1")
                val target1260 = branch("release/1.26.0")

                assertEquals(
                    dependency1260,
                    sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target1260, "ANY")
                )

                assertEquals(
                    dependency1251,
                    sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target1251, "ANY")
                )

                assertEquals(
                    dependency1251,
                    sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target1250, "ANY")
                )
            }
        }

    }

    @Test
    fun `Different levels`() {
        project {
            val dependencyProject = this
            /* val dependency1250 = */ branch("release/1.25.0")
            /* val dependency1260 = */ branch("release/1.26.0")
            val dependency1261 = branch("release/1.26.1")
            project {
                branch("release/1.26") {
                    val target = this
                    assertEquals(
                        dependency1261,
                        sameReleaseBranchSource.getLatestBranch("2", dependencyProject, target, "ANY")
                    )
                    assertEquals(
                        dependency1261,
                        sameReleaseBranchSource.getLatestBranch("1", dependencyProject, target, "ANY")
                    )
                    assertEquals(
                        dependency1261,
                        sameReleaseBranchSource.getLatestBranch("", dependencyProject, target, "ANY")
                    )
                }
            }
        }
    }

}