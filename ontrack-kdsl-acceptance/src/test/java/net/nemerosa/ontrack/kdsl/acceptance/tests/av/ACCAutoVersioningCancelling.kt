package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHub
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test

/**
 * Testing the cancellation of requests when they start to pile up
 */
@TestOnGitHub
class ACCAutoVersioningCancelling : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning auto cancellation`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                    targetPropertyType = "properties",
                                )
                            )
                        )

                        // Creates several promotions in a row
                        (1..5).forEach { no ->
                            dependency.apply {
                                build(name = "1.0.$no") {
                                    promote("IRON")
                                }
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            fileContains("gradle.properties") {
                                "some-version = 1.0.5"
                            }
                        }

                        // Checks the first order has been processed
                        waitUntil(
                            task = "1.0.1 has been merged"
                        ) {
                            val entry = ontrack.autoVersioning.audit.entries(
                                source = dependency.project.name,
                                project = project.name,
                                branch = name,
                                version = "1.0.1",
                            ).firstOrNull()
                            entry != null && entry.mostRecentState.state == "PR_MERGED"
                        }

                        // Checks the previous orders have been cancelled
                        (2..4).forEach { no ->
                            waitUntil(
                                task = "1.0.$no has been cancelled"
                            ) {
                                val entry = ontrack.autoVersioning.audit.entries(
                                    source = dependency.project.name,
                                    project = project.name,
                                    branch = name,
                                    version = "1.0.$no",
                                ).firstOrNull()
                                entry != null && entry.mostRecentState.state == "PROCESSING_CANCELLED"
                            }
                        }

                        // Checks the last order has been processed
                        waitUntil(
                            task = "1.0.5 has been merged"
                        ) {
                            val entry = ontrack.autoVersioning.audit.entries(
                                source = dependency.project.name,
                                project = project.name,
                                branch = name,
                                version = "1.0.5",
                            ).firstOrNull()
                            entry != null && entry.mostRecentState.state == "PR_MERGED"
                        }
                    }
                }
            }
        }

    }

}