package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoVersioningTrailGQLPromotionRunFieldContributorIT : AbstractAutoVersioningTestSupport() {

    @Test
    fun `Getting the auto-versioning trail and audit for a promotion run`() {
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
                                                "id" to app2.id(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app2.properties"
                                            ),
                                            "rejectionReason" to null,
                                        ),
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id(),
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
                                                "id" to app2.id(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app2.properties"
                                            ),
                                            "rejectionReason" to "Branch is disabled",
                                        ),
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id(),
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