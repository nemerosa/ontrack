package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import kotlin.test.fail

class ACCAutoVersioningSameRelease : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Old versions must not be targeted by newer versions`() {

        val scmBranches = listOf(
            "release/1.25.0",
            "release/1.25.1",
            "release/1.26.0",
        )

        val depProject = withMockScmRepository(ontrack) {
            project {
                configuredForMockScm()
                scmBranches.forEach { scmBranch ->
                    branch(name = scmBranch.replace("/", "-")) {
                        configuredForMockRepository(scmBranch = scmBranch)
                        promotion(name = "IRON")
                    }
                }
                this
            }
        }

        withMockScmRepository(ontrack) {
            withAutoVersioning {
                scmBranches.forEach { scmBranch ->
                    repositoryFile("gradle.properties", branch = scmBranch) {
                        "depVersion = 1.24.0" // Forcing an AV version if eligible
                    }
                }
                project {
                    configuredForMockScm()
                    scmBranches.forEach { scmBranch ->
                        branch(name = scmBranch.replace("/", "-")) {
                            configuredForMockRepository(scmBranch = scmBranch)
                            setAutoVersioningConfig(
                                listOf(
                                    AutoVersioningSourceConfig(
                                        sourceProject = depProject.name,
                                        sourceBranch = "&same-release:2",
                                        sourcePromotion = "IRON",
                                        targetPath = "gradle.properties",
                                        targetProperty = "depVersion",
                                        targetPropertyType = "properties",
                                    )
                                )
                            )
                        }
                    }

                    // Creating a build in latest dependency branch and promoting it
                    val dep = ontrack.findBranchByName(depProject.name, "release-1.26.0")
                        ?: fail("Cannot find dependency branch")
                    val depBuild = dep.createBuild("1.26.0")
                    depBuild.promote("IRON")

                    // Waiting for all AV requests to complete
                    waitForAutoVersioningCompletion()

                    // Checks
                    assertThatMockScmRepository {
                        // That's the only we expect
                        fileContains("gradle.properties", branch = "release/1.26.0") {
                            "depVersion = 1.26.0"
                        }
                        // Other must NOT have updated
                        fileContains("gradle.properties", branch = "release/1.25.1") {
                            "depVersion = 1.24.0"
                        }
                        fileContains("gradle.properties", branch = "release/1.25.0") {
                            "depVersion = 1.24.0"
                        }
                    }
                }
            }
        }
    }

}