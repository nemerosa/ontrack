package net.nemerosa.ontrack.extension.environments.promotions

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EnvironmentsBuildPromotionInfoExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var buildPromotionInfoService: BuildPromotionInfoService

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Test
    fun `Getting the promotion info for a build with some promotions`() {
        asAdmin {
            project {
                branch {
                    val bronze = promotionLevel("BRONZE")
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")

                    val eligibleSlotWithNoPromotionRule = slotTestSupport.withSlot(
                        order = 10,
                        project = project,
                    ) {}

                    val eligibleSlotWithSilverPromotionRule = slotTestSupport.withSlot(
                        order = 20,
                        project = project,
                    ) {
                        slotService.addAdmissionRuleConfig(
                            SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(
                                slot = it,
                                promotion = silver.name,
                            )
                        )
                    }

                    /* val nonEligibleSlotOnSameProject = */ slotTestSupport.withSlot(
                    order = 30,
                    project = project,
                ) {
                    slotService.addAdmissionRuleConfig(
                        SlotAdmissionRuleTestFixtures.testBranchPatternAdmissionRuleConfig(
                            slot = it,
                            includes = listOf("release-.*"),
                        )
                    )
                }

                    build {
                        val runBronze1 = promote(bronze)
                        val runBronze2 = promote(bronze)
                        val runSilver = promote(silver)

                        val eligibleSlotWithNoPromotionRulePipeline = slotService.startPipeline(
                            slot = eligibleSlotWithNoPromotionRule,
                            build = this,
                        )

                        val eligibleSlotWithSilverPromotionRulePipelines = (1..2).map {
                            slotService.startPipeline(
                                slot = eligibleSlotWithSilverPromotionRule,
                                build = this,
                            )
                        }

                        val info = buildPromotionInfoService.getBuildPromotionInfo(this)

                        // Getting the pipelines & slots not linked to promotions
                        assertEquals(2, info.noPromotionItems.size)
                        info.noPromotionItems[0].apply {
                            assertIs<Slot>(data) {
                                assertEquals(eligibleSlotWithNoPromotionRule, it)
                            }
                        }
                        info.noPromotionItems[1].apply {
                            assertIs<SlotPipeline>(data) {
                                assertEquals(eligibleSlotWithNoPromotionRulePipeline.id, it.id)
                            }
                        }

                        // Checking each promotion
                        assertEquals(3, info.withPromotionItems.size)

                        val expectedGold = info.withPromotionItems[0]
                        assertEquals(gold, expectedGold.promotionLevel)
                        assertEquals(1, expectedGold.items.size)
                        val goldPromotion = expectedGold.items.first()
                        assertIs<PromotionLevel>(goldPromotion.data) {
                            assertEquals(gold, it)
                        }

                        val expectedSilver = info.withPromotionItems[1]
                        assertEquals(silver, expectedSilver.promotionLevel)
                        val expectedSilverItems = expectedSilver.items
                        assertEquals(5, expectedSilverItems.size)
                        expectedSilverItems[0].apply {
                            assertIs<Slot>(data) {
                                assertEquals(eligibleSlotWithSilverPromotionRule, it)
                            }
                        }
                        expectedSilverItems[1].apply {
                            assertIs<SlotPipeline>(data) {
                                assertEquals(eligibleSlotWithSilverPromotionRulePipelines[1].id, it.id)
                            }
                        }
                        expectedSilverItems[2].apply {
                            assertIs<SlotPipeline>(data) {
                                assertEquals(eligibleSlotWithSilverPromotionRulePipelines[0].id, it.id)
                            }
                        }
                        expectedSilverItems[3].apply {
                            assertIs<PromotionLevel>(data) {
                                assertEquals(silver, it)
                            }
                        }
                        expectedSilverItems[4].apply {
                            assertIs<PromotionRun>(data) {
                                assertEquals(runSilver, it)
                            }
                        }

                        val expectedBronze = info.withPromotionItems[2]
                        assertEquals(bronze, expectedBronze.promotionLevel)
                        val expectedBronzeItems = expectedBronze.items
                        assertEquals(3, expectedBronzeItems.size)
                        expectedBronzeItems[0].apply {
                            assertIs<PromotionLevel>(data) {
                                assertEquals(bronze, it)
                            }
                        }
                        expectedBronzeItems[1].apply {
                            assertIs<PromotionRun>(data) {
                                assertEquals(runBronze2, it)
                            }
                        }
                        expectedBronzeItems[2].apply {
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