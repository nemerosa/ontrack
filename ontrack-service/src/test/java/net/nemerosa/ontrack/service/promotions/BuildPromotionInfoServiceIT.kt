package net.nemerosa.ontrack.service.promotions

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildPromotionInfoServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var buildPromotionInfoService: BuildPromotionInfoService

    @Test
    fun `Getting the promotion info for a build with no promotion at all`() {
        asAdmin {
            project {
                branch {
                    build {
                        val info = buildPromotionInfoService.getBuildPromotionInfo(this)

                        // By default, without extensions, no info without promotion
                        assertTrue(
                            info.noPromotionItems.isEmpty(),
                            "Promotion info without promotions should not be empty"
                        )

                        // No promotion item expected
                        assertEquals(0, info.withPromotionItems.size)
                    }
                }
            }
        }
    }

    @Test
    fun `Getting the promotion info for a build with no promotion`() {
        asAdmin {
            project {
                branch {
                    val bronze = promotionLevel("BRONZE")
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    build {
                        val info = buildPromotionInfoService.getBuildPromotionInfo(this)

                        // By default, without extensions, no info without promotion
                        assertTrue(
                            info.noPromotionItems.isEmpty(),
                            "Promotion info without promotions should not be empty"
                        )

                        // Checking each promotion
                        assertEquals(3, info.withPromotionItems.size)

                        val expectedGold = info.withPromotionItems[0]
                        assertEquals(gold, expectedGold.promotionLevel)
                        assertEquals(1, expectedGold.items.size)
                        val goldPromotion = expectedGold.items.first()
                        assertEquals("promotionLevel", goldPromotion.type)
                        assertIs<PromotionLevel>(goldPromotion.data) {
                            assertEquals(gold, it)
                        }

                        val expectedSilver = info.withPromotionItems[1]
                        assertEquals(silver, expectedSilver.promotionLevel)
                        val expectedSilverItems = expectedSilver.items
                        assertEquals(1, expectedSilverItems.size)
                        expectedSilverItems[0].apply {
                            assertEquals("promotionLevel", type)
                            assertIs<PromotionLevel>(data) {
                                assertEquals(silver, it)
                            }
                        }

                        val expectedBronze = info.withPromotionItems[2]
                        assertEquals(bronze, expectedBronze.promotionLevel)
                        val expectedBronzeItems = expectedBronze.items
                        assertEquals(1, expectedBronzeItems.size)
                        expectedBronzeItems[0].apply {
                            assertEquals("promotionLevel", type)
                            assertIs<PromotionLevel>(data) {
                                assertEquals(bronze, it)
                            }
                        }
                    }
                }
            }
        }
    }

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

                        val info = buildPromotionInfoService.getBuildPromotionInfo(this)

                        // By default, without extensions, no info without promotion
                        assertTrue(
                            info.noPromotionItems.isEmpty(),
                            "Promotion info without promotions should not be empty"
                        )

                        // Checking each promotion
                        assertEquals(3, info.withPromotionItems.size)

                        val expectedGold = info.withPromotionItems[0]
                        assertEquals(gold, expectedGold.promotionLevel)
                        assertEquals(1, expectedGold.items.size)
                        val goldPromotion = expectedGold.items.first()
                        assertEquals("promotionLevel", goldPromotion.type)
                        assertIs<PromotionLevel>(goldPromotion.data) {
                            assertEquals(gold, it)
                        }

                        val expectedSilver = info.withPromotionItems[1]
                        assertEquals(silver, expectedSilver.promotionLevel)
                        val expectedSilverItems = expectedSilver.items
                        assertEquals(2, expectedSilverItems.size)
                        expectedSilverItems[0].apply {
                            assertEquals("promotionLevel", type)
                            assertIs<PromotionLevel>(data) {
                                assertEquals(silver, it)
                            }
                        }
                        expectedSilverItems[1].apply {
                            assertEquals("promotionRun", type)
                            assertIs<PromotionRun>(data) {
                                assertEquals(runSilver, it)
                            }
                        }

                        val expectedBronze = info.withPromotionItems[2]
                        assertEquals(bronze, expectedBronze.promotionLevel)
                        val expectedBronzeItems = expectedBronze.items
                        assertEquals(3, expectedBronzeItems.size)
                        expectedBronzeItems[0].apply {
                            assertEquals("promotionLevel", type)
                            assertIs<PromotionLevel>(data) {
                                assertEquals(bronze, it)
                            }
                        }
                        expectedBronzeItems[1].apply {
                            assertEquals("promotionRun", type)
                            assertIs<PromotionRun>(data) {
                                assertEquals(runBronze2, it)
                            }
                        }
                        expectedBronzeItems[2].apply {
                            assertEquals("promotionRun", type)
                            assertIs<PromotionRun>(data) {
                                assertEquals(runBronze1, it)
                            }
                        }
                    }
                }
            }
        }
    }

}