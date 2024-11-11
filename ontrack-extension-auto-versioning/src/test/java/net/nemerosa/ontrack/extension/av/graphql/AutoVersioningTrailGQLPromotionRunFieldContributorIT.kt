package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AutoVersioningTrailGQLPromotionRunFieldContributorIT : AbstractAutoVersioningTestSupport() {

    @Test
    fun `Getting the auto-versioning trail for a promotion run`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            val run = pl.run()
            run(
                """
                        {
                            promotionRuns(id: ${run.id}) {
                                autoVersioningTrail {
                                    branches {
                                        branch {
                                            id
                                        }
                                        configuration {
                                            targetPath
                                        }
                                        rejectionReason
                                    }
                                }
                            }
                        }
                    """, mapOf(

                )
            ) { data ->
                assertEquals(
                    mapOf(
                        "promotionRuns" to listOf(
                            mapOf(
                                "autoVersioningTrail" to mapOf(
                                    "branches" to listOf(
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app2.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app2.properties"
                                            ),
                                            "rejectionReason" to null,
                                        ),
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app1.properties"
                                            ),
                                            "rejectionReason" to null,
                                        ),
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Getting the auto-versioning trail and audit for a promotion run`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            structureService.disableBranch(app2)
            val run = pl.run()
            run(
                """
                        {
                            promotionRuns(id: ${run.id}) {
                                autoVersioningTrail {
                                    branches {
                                        branch {
                                            id
                                        }
                                        rejectionReason
                                        orderId
                                        audit {
                                          order {
                                            uuid
                                          }
                                          mostRecentState {
                                            state
                                          }
                                        }
                                    }
                                }
                            }
                        }
                    """, mapOf(

                )
            ) { data ->
                val branches = data.path("promotionRuns").path(0)
                    .path("autoVersioningTrail").path("branches")
                assertNotNull(branches.find { it.path("rejectionReason").isNull }) { branch ->
                    assertEquals(app1.id(), branch.path("branch").path("id").asInt())
                    val orderId = branch.path("orderId").asText()
                    val audit = branch.path("audit")
                    assertEquals(orderId, audit.path("order").path("uuid").asText())
                }
            }
        }
    }

    @Test
    fun `Getting the auto-versioning trail and audit for a promotion run with one target being disabled`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            structureService.disableBranch(app2)
            val run = pl.run()
            run(
                """
                        {
                            promotionRuns(id: ${run.id}) {
                                autoVersioningTrail {
                                    branches {
                                        branch {
                                            id
                                        }
                                        configuration {
                                            targetPath
                                        }
                                        rejectionReason
                                    }
                                }
                            }
                        }
                    """, mapOf(

                )
            ) { data ->
                assertEquals(
                    mapOf(
                        "promotionRuns" to listOf(
                            mapOf(
                                "autoVersioningTrail" to mapOf(
                                    "branches" to listOf(
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app2.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app2.properties"
                                            ),
                                            "rejectionReason" to "Branch is disabled",
                                        ),
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app1.properties"
                                            ),
                                            "rejectionReason" to null,
                                        ),
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }
}