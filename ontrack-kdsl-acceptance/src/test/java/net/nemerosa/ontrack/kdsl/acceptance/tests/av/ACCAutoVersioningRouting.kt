package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlayground
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestOnGitHubPlayground
class ACCAutoVersioningRouting : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning using a specific routing`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                // Delete the existing project if any
                val projectName = "test-av-routing"
                ontrack.findProjectByName(projectName)?.delete()

                project(name = projectName) {
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

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            fileContains("gradle.properties") {
                                "some-version = 2.0.0"
                            }
                        }

                        // Checks the routing key of the auto versioning audit
                        waitUntil(
                            task = "Waiting for full processing"
                        ) {
                            val entry = ontrack.autoVersioning.audit.entries(project = projectName).firstOrNull()
                            entry != null && entry.mostRecentState.state == "PR_MERGED"
                        }
                        val entry = ontrack.autoVersioning.audit.entries(project = projectName).firstOrNull()
                        assertNotNull(entry) {
                            assertEquals("project.$projectName", it.routing)
                            assertEquals("auto-versioning.project.$projectName", it.queue)
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning using default routing`() {
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

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            fileContains("gradle.properties") {
                                "some-version = 2.0.0"
                            }
                        }

                        // Checks the routing key of the auto versioning audit
                        val expectedIndex = this@branch.id.toInt() % 10 + 1
                        val entry = ontrack.autoVersioning.audit.entries(project = project.name).firstOrNull()
                        assertNotNull(entry) {
                            assertEquals("default.$expectedIndex", it.routing)
                            assertEquals("auto-versioning.default.$expectedIndex", it.queue)
                        }

                    }
                }
            }
        }
    }

}