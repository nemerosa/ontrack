package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test

/**
 * Testing the throttling of requests when they start to pile up
 */
class ACCAutoVersioningThrottling : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Throttling of requests`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                    targetPropertyType = "properties",
                                    postProcessing = "mock",
                                    postProcessingConfig = mapOf(
                                        "postProcessingStamp" to uid("ps_"),
                                        "durationMs" to 1_000L, // Forcing a delay in post-processing
                                    ).asJson()
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

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                "some-version = 1.0.5"
                            }
                        }

                        // Checks the first order has been processed
                        waitUntil(
                            task = "1.0.1 has been merged",
                            timeout = 10_000L,
                            interval = 1_000L,
                        ) {
                            val entry = ontrack.autoVersioning.audit.entries(
                                source = dependency.project.name,
                                project = project.name,
                                branch = name,
                                version = "1.0.1",
                            ).firstOrNull()
                            entry != null && entry.mostRecentState.state == "PR_MERGED"
                        }

                        // Checks the previous orders have been throttled
                        (2..4).forEach { no ->
                            waitUntil(
                                task = "1.0.$no has been throttled",
                                timeout = 10_000L,
                                interval = 1_000L,
                                onTimeout = {
                                    println("Entries:")
                                    ontrack.autoVersioning.audit.entries(
                                        source = dependency.project.name,
                                        project = project.name,
                                        branch = name,
                                    ).forEach {
                                        println("* $it")
                                    }
                                }
                            ) {
                                val entry = ontrack.autoVersioning.audit.entries(
                                    source = dependency.project.name,
                                    project = project.name,
                                    branch = name,
                                    version = "1.0.$no",
                                ).firstOrNull()
                                entry != null && entry.mostRecentState.state == "THROTTLED"
                            }
                        }

                        // Checks the last order has been processed
                        waitUntil(
                            task = "1.0.5 has been merged",
                            timeout = 10_000L,
                            interval = 1_000L,
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