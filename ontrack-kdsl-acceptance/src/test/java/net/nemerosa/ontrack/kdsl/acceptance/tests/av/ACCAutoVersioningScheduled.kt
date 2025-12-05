package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testing the scheduled auto-versioning requests
 */
class ACCAutoVersioningScheduled : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Scheduled auto-versioning`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                val now = Time.now

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
                                    cronSchedule = cronForTime(now.plusMinutes(5)),
                                )
                            )
                        )

                        // Launching the AV request
                        dependency.apply {
                            build(name = "1.0.1") {
                                promote("IRON")
                            }
                        }

                        // Checks that the order has been created but not scheduled yet
                        assertNotNull(
                            ontrack.autoVersioning.audit.entries(
                                source = dependency.project.name,
                                project = project.name,
                                branch = name,
                                version = "1.0.1",
                            ).firstOrNull(),
                            "AV audit entry created"
                        ) {
                            assertEquals(
                                "CREATED",
                                it.mostRecentState.state,
                            )
                        }

                        // Forcing the scheduling at a next time
                        ontrack.autoVersioning.schedule(now.plusMinutes(10))

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                "some-version = 1.0.1"
                            }
                        }

                        // Checks the order has been processed
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
                    }
                }
            }
        }
    }

    @Test
    fun `Throttling of scheduled auto-versioning requests`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                val now = Time.now

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
                                    cronSchedule = cronForTime(now.plusHours(5)),
                                )
                            )
                        )

                        // Launching the multiple AV requests
                        dependency.apply {
                            (1..5).forEach { no ->
                                build(name = "1.0.$no") {
                                    promote("IRON")
                                }
                            }
                        }

                        // Checks the orders
                        // * the 1.0.5 is created
                        // * the other ones are flagged as throttled
                        (1..5).forEach { no ->
                            assertNotNull(
                                ontrack.autoVersioning.audit.entries(
                                    source = dependency.project.name,
                                    project = project.name,
                                    branch = name,
                                    version = "1.0.$no",
                                ).firstOrNull(),
                                "AV audit entry created"
                            ) {
                                val expectedState = if (no == 5) "CREATED" else "THROTTLED"
                                assertEquals(
                                    expectedState,
                                    it.mostRecentState.state,
                                )
                            }
                        }

                        // Forcing the scheduling at a next time
                        ontrack.autoVersioning.schedule(now.plusHours(10))

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                "some-version = 1.0.5"
                            }
                        }

                        // Checks the order has been processed
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

    private fun cronForTime(time: LocalDateTime): String {
        val second = time.second
        val minute = time.minute
        val hour = time.hour
        val dayOfMonth = time.dayOfMonth
        val month = time.monthValue
        val dayOfWeek = time.dayOfWeek.value % 7  // Convert to cron format (0 = Sunday, 1 = Monday, etc.)

        return "$second $minute $hour $dayOfMonth $month $dayOfWeek"
    }

}