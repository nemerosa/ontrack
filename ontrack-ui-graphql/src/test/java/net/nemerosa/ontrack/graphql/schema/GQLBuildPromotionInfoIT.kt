package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLBuildPromotionInfoIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting the promotion info for a build with some promotions`() {
        asAdmin {
            project {
                branch {
                    val bronze = promotionLevel("BRONZE")
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    build {
                        val runBronze1 = promote(bronze)
                        val runBronze2 = promote(bronze)
                        val runSilver = promote(silver)

                        run(
                            """
                                fragment BuildPromotionInfoItemDataContent on BuildPromotionInfoItemData {
                                    __typename
                                    ... on PromotionLevel {
                                        id
                                    }
                                    ... on PromotionRun {
                                        id
                                    }
                                }
                                query BuildPromotionInfo {
                                    build(id: $id) {
                                        promotionInfo {
                                            items {
                                                promotionLevel {
                                                    name
                                                }
                                                data {
                                                    ...BuildPromotionInfoItemDataContent
                                                }
                                            }
                                        }
                                    }
                                }
                            """.trimIndent()
                        ) { data ->
                            assertEquals(
                                mapOf(
                                    "build" to mapOf(
                                        "promotionInfo" to mapOf(
                                            "items" to listOf(
                                                mapOf(
                                                    "promotionLevel" to mapOf(
                                                        "name" to gold.name,
                                                    ),
                                                    "data" to mapOf(
                                                        "__typename" to "PromotionLevel",
                                                        "id" to gold.id.toString()
                                                    )
                                                ),
                                                mapOf(
                                                    "promotionLevel" to mapOf(
                                                        "name" to silver.name,
                                                    ),
                                                    "data" to mapOf(
                                                        "__typename" to "PromotionRun",
                                                        "id" to runSilver.id.toString()
                                                    )
                                                ),
                                                mapOf(
                                                    "promotionLevel" to mapOf(
                                                        "name" to bronze.name,
                                                    ),
                                                    "data" to mapOf(
                                                        "__typename" to "PromotionRun",
                                                        "id" to runBronze2.id.toString()
                                                    )
                                                ),
                                                mapOf(
                                                    "promotionLevel" to mapOf(
                                                        "name" to bronze.name,
                                                    ),
                                                    "data" to mapOf(
                                                        "__typename" to "PromotionRun",
                                                        "id" to runBronze1.id.toString()
                                                    )
                                                ),
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
        }
    }

}