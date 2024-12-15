package net.nemerosa.ontrack.extension.environments.promotions

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.settings.EnvironmentsSettings
import net.nemerosa.ontrack.extension.environments.settings.EnvironmentsSettingsBuildDisplayOption
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfo
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

@Component
class EnvironmentsBuildPromotionInfoExtensionTestSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var buildPromotionInfoService: BuildPromotionInfoService

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    fun withSetup(
        buildDisplayOption: EnvironmentsSettingsBuildDisplayOption,
        deployed: Boolean = true,
        test: (info: TestInfo) -> Unit,
    ) {
        asAdmin {
            withSettings<EnvironmentsSettings> {
                settingsManagerService.saveSettings(
                    EnvironmentsSettings(
                        buildDisplayOption = buildDisplayOption,
                    )
                )
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

                            val eligibleSlotWithNoPromotionRulePipeline = if (deployed) {
                                slotService.startPipeline(
                                    slot = eligibleSlotWithNoPromotionRule,
                                    build = this,
                                ).apply {
                                    slotTestSupport.startAndDeployPipeline(this)
                                }
                            } else {
                                null
                            }

                            val eligibleSlotWithSilverPromotionRulePipeline = if (deployed) {
                                slotService.startPipeline(
                                    slot = eligibleSlotWithSilverPromotionRule,
                                    build = this,
                                ).apply {
                                    slotTestSupport.startAndDeployPipeline(this)
                                }
                            } else {
                                null
                            }

                            // Checking that everything is marked as deployed
                            val pipelines = slotService.findSlotPipelinesWhereBuildIsLastDeployed(this)
                            val count = if (deployed) 2 else 0
                            assertEquals(count, pipelines.size, "All pipelines deployed")

                            val info = buildPromotionInfoService.getBuildPromotionInfo(this)

                            // Testing the info
                            test(
                                TestInfo(
                                    build = this,
                                    info = info,
                                    bronze = bronze,
                                    silver = silver,
                                    gold = gold,
                                    runBronze1 = runBronze1,
                                    runBronze2 = runBronze2,
                                    runSilver = runSilver,
                                    eligibleSlotWithNoPromotionRule = eligibleSlotWithNoPromotionRule,
                                    eligibleSlotWithNoPromotionRulePipeline = eligibleSlotWithNoPromotionRulePipeline,
                                    eligibleSlotWithSilverPromotionRule = eligibleSlotWithSilverPromotionRule,
                                    eligibleSlotWithSilverPromotionRulePipeline = eligibleSlotWithSilverPromotionRulePipeline,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    data class TestInfo(
        val build: Build,
        val info: BuildPromotionInfo,
        val bronze: PromotionLevel,
        val silver: PromotionLevel,
        val gold: PromotionLevel,
        val runBronze1: PromotionRun,
        val runBronze2: PromotionRun,
        val runSilver: PromotionRun,
        val eligibleSlotWithNoPromotionRule: Slot,
        val eligibleSlotWithNoPromotionRulePipeline: SlotPipeline?,
        val eligibleSlotWithSilverPromotionRule: Slot,
        val eligibleSlotWithSilverPromotionRulePipeline: SlotPipeline?,
    )

}