package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoVersioningTrailGQLPromotionLevelFieldContributorIT : AbstractAutoVersioningTestSupport() {

    @Test
    fun `Getting the auto-versioning targets for a promotion level`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            run(
                """
                    {
                        promotionLevel(id: ${pl.id}) {
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
                """
            ) { data ->
                assertEquals(
                    mapOf(
                        "promotionLevel" to mapOf(
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
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Getting the auto-versioning targets for a promotion level with a target being disabled`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            structureService.disableBranch(app2)
            run(
                """
                        {
                            promotionLevel(id: ${pl.id}) {
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
                        "promotionLevel" to mapOf(
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
                    ).asJson(),
                    data
                )
            }
        }
    }
}