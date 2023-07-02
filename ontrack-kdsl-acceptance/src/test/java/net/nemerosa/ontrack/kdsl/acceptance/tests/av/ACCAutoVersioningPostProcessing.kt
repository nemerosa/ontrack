package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test

class ACCAutoVersioningPostProcessing : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning post processing`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                // Ontrack as a dependency
                val ontrackDep = branchWithPromotion(promotion = "RELEASE")
                // Sample project to automatically upgrade & post-process
                val postProcessingStamp = uid("ps_")
                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = ontrackDep.project.name,
                                    sourceBranch = ontrackDep.name,
                                    sourcePromotion = "RELEASE",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                    postProcessing = "mock",
                                    postProcessingConfig = mapOf(
                                        "postProcessingStamp" to postProcessingStamp,
                                    ).asJson(),
                                )
                            )
                        )

                        // Creates a new promoted version of the dependency
                        ontrackDep.apply {
                            build(name = "2.0.0") {
                                promote("RELEASE")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                "some-version = 2.0.0"
                            }
                            fileContains("post-processing.properties") {
                                "postProcessingStamp = $postProcessingStamp"
                            }
                        }
                    }
                }
            }
        }
    }

}