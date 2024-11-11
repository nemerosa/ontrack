package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
                            fragment BuildPromotionInfoItemData on BuildPromotionInfoItem {
                                __typename
                                ... on PromotionLevel {
                                    name
                                }
                                ... on PromotionRun {
                                    id
                                }
                            }
                            query BuildPromotionInfo {
                                build(id: $id) {
                                    promotionInfo {
                                        noPromotionItems {
                                            ...BuildPromotionInfoItemData
                                        }
                                        withPromotionItems {
                                            promotionLevel {
                                                name
                                            }
                                            items {
                                                ...BuildPromotionInfoItemData
                                            }
                                        }
                                    }
                                }
                            }
                            """.trimIndent()
                        ) { data ->

                            val buildPromotionInfo = data.path("build")
                                .path("promotionInfo")

                            // By default, without extensions, no info without promotion
                            assertTrue(
                                buildPromotionInfo.path("noPromotionItems").isEmpty(),
                                "Promotion info without promotions should not be empty"
                            )

                            // Checking each promotion
                            assertEquals(3, buildPromotionInfo.path("withPromotionItems").size())

                            val expectedGold = buildPromotionInfo.path("withPromotionItems").path(0)
                            assertEquals(gold.name, expectedGold.path("promotionLevel").path("name").asText())
                            assertEquals(1, expectedGold.path("items").size())
                            val goldPromotion = expectedGold.path("items").path(0)
                            assertEquals("PromotionLevel", goldPromotion.path("__typename").asText())
                            assertEquals(gold.name, goldPromotion.path("name").asText())

                            val expectedSilver = buildPromotionInfo.path("withPromotionItems").path(1)
                            assertEquals(silver.name, expectedSilver.path("promotionLevel").path("name").asText())
                            val expectedSilverItems = expectedSilver.path("items")
                            assertEquals(2, expectedSilverItems.size())
                            expectedSilverItems[0].apply {
                                assertEquals("PromotionLevel", path("__typename").asText())
                                assertEquals(silver.name, path("name").asText())
                            }
                            expectedSilverItems[1].apply {
                                assertEquals("PromotionRun", path("__typename").asText())
                                assertEquals(runSilver.id(), path("id").asInt())
                            }

                            val expectedBronze = buildPromotionInfo.path("withPromotionItems").path(2)
                            assertEquals(bronze.name, expectedBronze.path("promotionLevel").path("name").asText())
                            val expectedBronzeItems = expectedBronze.path("items")
                            assertEquals(3, expectedBronzeItems.size())
                            expectedBronzeItems[0].apply {
                                assertEquals("PromotionLevel", path("__typename").asText())
                                assertEquals(bronze.name, path("name").asText())
                            }
                            expectedBronzeItems[1].apply {
                                assertEquals("PromotionRun", path("__typename").asText())
                                assertEquals(runBronze2.id(), path("id").asInt())
                            }
                            expectedBronzeItems[2].apply {
                                assertEquals("PromotionRun", path("__typename").asText())
                                assertEquals(runBronze1.id(), path("id").asInt())
                            }
                        }
                    }
                }
            }
        }
    }

}