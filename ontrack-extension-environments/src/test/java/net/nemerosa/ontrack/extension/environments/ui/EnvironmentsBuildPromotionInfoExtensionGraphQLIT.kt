package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EnvironmentsBuildPromotionInfoExtensionGraphQLIT : AbstractQLKTITSupport() {

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
                                    ... on Slot {
                                        id
                                    }
                                    ... on SlotPipeline {
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

                            assertEquals(
                                mapOf(
                                    "build" to mapOf(
                                        "promotionInfo" to mapOf(
                                            "noPromotionItems" to listOf(
                                                mapOf(
                                                    "__typename" to "Slot",
                                                    "id" to eligibleSlotWithNoPromotionRule.id,
                                                ),
                                                mapOf(
                                                    "__typename" to "SlotPipeline",
                                                    "id" to eligibleSlotWithNoPromotionRulePipeline.id,
                                                ),
                                            ),
                                            "withPromotionItems" to listOf(
                                                mapOf(
                                                    "promotionLevel" to mapOf(
                                                        "name" to gold.name,
                                                    ),
                                                    "items" to listOf(
                                                        mapOf(
                                                            "__typename" to "PromotionLevel",
                                                            "name" to gold.name,
                                                        ),
                                                    )
                                                ),
                                                mapOf(
                                                    "promotionLevel" to mapOf(
                                                        "name" to silver.name,
                                                    ),
                                                    "items" to listOf(
                                                        mapOf(
                                                            "__typename" to "Slot",
                                                            "id" to eligibleSlotWithSilverPromotionRule.id,
                                                        ),
                                                        mapOf(
                                                            "__typename" to "SlotPipeline",
                                                            "id" to eligibleSlotWithSilverPromotionRulePipelines[1].id,
                                                        ),
                                                        mapOf(
                                                            "__typename" to "SlotPipeline",
                                                            "id" to eligibleSlotWithSilverPromotionRulePipelines[0].id,
                                                        ),
                                                        mapOf(
                                                            "__typename" to "PromotionLevel",
                                                            "name" to silver.name,
                                                        ),
                                                        mapOf(
                                                            "__typename" to "PromotionRun",
                                                            "id" to runSilver.id,
                                                        ),
                                                    )
                                                ),
                                                mapOf(
                                                    "promotionLevel" to mapOf(
                                                        "name" to bronze.name,
                                                    ),
                                                    "items" to listOf(
                                                        mapOf(
                                                            "__typename" to "PromotionLevel",
                                                            "name" to bronze.name,
                                                        ),
                                                        mapOf(
                                                            "__typename" to "PromotionRun",
                                                            "id" to runBronze2.id,
                                                        ),
                                                        mapOf(
                                                            "__typename" to "PromotionRun",
                                                            "id" to runBronze1.id,
                                                        ),
                                                    )
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
        }
    }
}