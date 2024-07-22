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
                                potentialTargetBranches {
                                    id
                                    autoVersioningConfig {
                                        configurations {
                                            targetPath
                                        }
                                    }
                                }
                                rejectedTargetBranches {
                                    branch {
                                        id
                                    }
                                    reason
                                }
                            }
                        }
                    }
                """
            ) { data ->
                assertEquals(
                    mapOf(
                        "promotionRuns" to listOf(
                            mapOf(
                                "autoVersioningTrail" to mapOf(
                                    "potentialTargetBranches" to listOf(
                                        mapOf(
                                            "id" to app2.id(),
                                            "autoVersioningConfig" to mapOf(
                                                "configurations" to listOf(
                                                    mapOf(
                                                        "targetPath" to "app2.properties"
                                                    )
                                                )
                                            )
                                        ),
                                        mapOf(
                                            "id" to app1.id(),
                                            "autoVersioningConfig" to mapOf(
                                                "configurations" to listOf(
                                                    mapOf(
                                                        "targetPath" to "app1.properties"
                                                    )
                                                )
                                            )
                                        ),
                                    ),
                                    "rejectedTargetBranches" to emptyList()
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
                                    potentialTargetBranches {
                                        id
                                        autoVersioningConfig {
                                            configurations {
                                                targetPath
                                            }
                                        }
                                    }
                                    rejectedTargetBranches {
                                        branch {
                                            id
                                        }
                                        reason
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
                                    "potentialTargetBranches" to listOf(
                                        mapOf(
                                            "id" to app2.id(),
                                            "autoVersioningConfig" to mapOf(
                                                "configurations" to listOf(
                                                    mapOf(
                                                        "targetPath" to "app2.properties"
                                                    )
                                                )
                                            )
                                        ),
                                        mapOf(
                                            "id" to app1.id(),
                                            "autoVersioningConfig" to mapOf(
                                                "configurations" to listOf(
                                                    mapOf(
                                                        "targetPath" to "app1.properties"
                                                    )
                                                )
                                            )
                                        ),
                                    ),
                                    "rejectedTargetBranches" to listOf(
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app2.id(),
                                            ),
                                            "reason" to "Branch is disabled"
                                        )
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