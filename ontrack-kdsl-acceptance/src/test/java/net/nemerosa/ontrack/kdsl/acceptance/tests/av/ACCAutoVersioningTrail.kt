package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.trail.autoVersioningTrail
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testing the access to the trail of an auto-versioning process.
 */
class ACCAutoVersioningTrail : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Getting the trail for an AV process`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                project {
                    branch {
                        val targetBranch = this
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
                                        "durationMs" to 1_000L, // Forcing a delay in processing
                                    ).asJson()
                                )
                            )
                        )

                        // Promotion
                        dependency.apply {
                            build(name = "2.0.0") {
                                val run = promote("IRON")

                                waitForAutoVersioningCompletion()

                                assertThatMockScmRepository {
                                    fileContains("gradle.properties") {
                                        "some-version = 2.0.0"
                                    }
                                }

                                // Getting the trail for this run
                                val trail = run.autoVersioningTrail
                                assertNotNull(trail, "Trail was available")

                                // Branch trail
                                assertEquals(1, trail.branches.size)
                                val branchTrail = trail.branches.first()
                                assertEquals(targetBranch.name, branchTrail.branch.name)

                                // Was not rejected
                                assertNull(branchTrail.rejectionReason)

                                // Getting the order ID
                                val orderId = branchTrail.orderId
                                assertNotNull(orderId, "AV order was placed")

                                // Getting the AV audit entry
                                assertNotNull(branchTrail.audit, "Audit entry was found") { entry ->
                                    assertEquals(orderId, entry.order.uuid)
                                    assertEquals("PR_MERGED", entry.mostRecentState.state)
                                }

                            }
                        }
                    }
                }
            }
        }

    }

}