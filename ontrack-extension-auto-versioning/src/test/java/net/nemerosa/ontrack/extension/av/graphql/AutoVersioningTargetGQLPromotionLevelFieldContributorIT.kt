package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoVersioningTargetGQLPromotionLevelFieldContributorIT : AbstractAutoVersioningTestSupport() {

    @Test
    fun `Getting the auto-versioning targets for a promotion level`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            run(
                """
                    {
                        promotionLevel(id: ${pl.id}) {
                            autoVersioningTargets {
                                branch {
                                    id
                                }
                                configuration {
                                    targetPath
                                }
                            }
                        }
                    }
                """
            ) { data ->
                assertEquals(
                    mapOf(
                        "promotionLevel" to mapOf(
                            "autoVersioningTargets" to listOf(
                                mapOf(
                                    "branch" to mapOf(
                                        "id" to app2.id(),
                                    ),
                                    "configuration" to mapOf(
                                        "targetPath" to "app2.properties",
                                    )
                                ),
                                mapOf(
                                    "branch" to mapOf(
                                        "id" to app1.id(),
                                    ),
                                    "configuration" to mapOf(
                                        "targetPath" to "app1.properties",
                                    )
                                ),
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
                                autoVersioningTargets {
                                    branch {
                                        id
                                    }
                                    configuration {
                                        targetPath
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
                            "autoVersioningTargets" to listOf(
                                mapOf(
                                    "branch" to mapOf(
                                        "id" to app1.id(),
                                    ),
                                    "configuration" to mapOf(
                                        "targetPath" to "app1.properties",
                                    )
                                ),
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }
}