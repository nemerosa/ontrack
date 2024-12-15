package net.nemerosa.ontrack.extension.environments.promotions

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

                        // Checking all items have been collected
                        assertEquals(9, info.items.size)

                        // First, the slots & their pipelines

                        info.items[0].apply {
                            assertEquals(silver, promotionLevel)
                            assertEquals(eligibleSlotWithSilverPromotionRule, data)
                        }

                        info.items[1].apply {
                            assertEquals(silver, promotionLevel)
                            assertEquals(eligibleSlotWithSilverPromotionRulePipelines[1].id, (data as SlotPipeline).id)
                        }

                        info.items[2].apply {
                            assertEquals(silver, promotionLevel)
                            assertEquals(eligibleSlotWithSilverPromotionRulePipelines[0].id, (data as SlotPipeline).id)
                        }

                        info.items[3].apply {
                            assertNull(promotionLevel)
                            assertEquals(eligibleSlotWithNoPromotionRule, data)
                        }

                        info.items[4].apply {
                            assertNull(promotionLevel)
                            assertEquals(eligibleSlotWithNoPromotionRulePipeline.id, (data as SlotPipeline).id)
                        }

                        // Then the promotions & their promotion runs

                        info.items[5].apply {
                            assertEquals(gold, promotionLevel)
                            assertEquals(gold, data)
                        }

                        info.items[6].apply {
                            assertEquals(silver, promotionLevel)
                            assertEquals(runSilver.id, (data as PromotionRun).id)
                        }

                        info.items[7].apply {
                            assertEquals(bronze, promotionLevel)
                            assertEquals(runBronze2.id, (data as PromotionRun).id)
                        }

                        info.items[8].apply {
                            assertEquals(bronze, promotionLevel)
                            assertEquals(runBronze1.id, (data as PromotionRun).id)
                        }
                    }
                }
            }
        }
    }

}