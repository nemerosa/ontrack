package net.nemerosa.ontrack.service.promotions

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

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
                        // No promotion item expected
                        assertEquals(0, info.items.size)
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

                        // Checking each promotion
                        assertEquals(3, info.items.size)

                        info.items[0].apply {
                            assertEquals(gold, promotionLevel)
                            assertEquals(gold, data)
                        }

                        info.items[1].apply {
                            assertEquals(silver, promotionLevel)
                            assertEquals(silver, data)
                        }

                        info.items[2].apply {
                            assertEquals(bronze, promotionLevel)
                            assertEquals(bronze, data)
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

                        // Checking each promotion
                        assertEquals(6, info.items.size)

                        info.items[0].apply {
                            assertEquals(gold, promotionLevel)
                            assertEquals(gold, data)
                        }

                        info.items[1].apply {
                            assertEquals(silver, promotionLevel)
                            assertEquals(silver, data)
                        }

                        info.items[2].apply {
                            assertEquals(silver, promotionLevel)
                            assertEquals(runSilver.id, (data as PromotionRun).id)
                        }

                        info.items[3].apply {
                            assertEquals(bronze, promotionLevel)
                            assertEquals(bronze, data)
                        }

                        info.items[4].apply {
                            assertEquals(bronze, promotionLevel)
                            assertEquals(runBronze2.id, (data as PromotionRun).id)
                        }

                        info.items[5].apply {
                            assertEquals(bronze, promotionLevel)
                            assertEquals(runBronze1.id, (data as PromotionRun).id)
                        }
                    }
                }
            }
        }
    }

}