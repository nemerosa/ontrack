package net.nemerosa.ontrack.extension.environments.promotions

import net.nemerosa.ontrack.extension.api.BuildPromotionInfoExtension
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.extension.environments.rules.PromotionRelatedSlotAdmissionRule
import net.nemerosa.ontrack.extension.environments.rules.SlotAdmissionRuleRegistry
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.springframework.stereotype.Component

@Component
class EnvironmentsBuildPromotionInfoExtension(
    extensionFeature: EnvironmentsExtensionFeature,
    private val slotService: SlotService,
    private val slotAdmissionRuleRegistry: SlotAdmissionRuleRegistry,
) : AbstractExtension(extensionFeature), BuildPromotionInfoExtension {

    companion object {
        const val BUILD_PROMOTION_INFO_ELIGIBLE_SLOT = "eligibleSlot"
        const val BUILD_PROMOTION_INFO_SLOT_PIPELINE = "slotPipeline"
    }

    override fun buildPromotionInfoItemsWithNoPromotion(build: Build): List<BuildPromotionInfoItem<*>> {
        // Items
        val items = mutableListOf<BuildPromotionInfoItem<*>>()
        // Getting the eligible slots which have NO rule on promotions
        val eligibleSlots = slotService.getEligibleSlotsForBuild(build)
            .filter { it.eligible }
            .map { it.slot }
            .filter {
                isSlotForNoPromotionLevel(it)
            }
            .sortedByDescending { it.environment.order }
        items += eligibleSlots.map {
            buildPromotionInfoItemForEligibleSlot(it)
        }
        // Getting the pipelines whose slots have NO rule on promotions
        val pipelines = slotService.findPipelineByBuild(build)
            .filter {
                isSlotForNoPromotionLevel(it.slot)
            }
            .sortedWith(
                compareByDescending<SlotPipeline> { it.slot.environment.order }
                    .thenByDescending { it.number }
            )
        items += pipelines.map {
            buildPromotionInfoItemForSlotPipeline(it)
        }
        // OK
        return items
    }

    override fun buildPromotionInfoItemsAfterPromotion(
        build: Build,
        promotionLevel: PromotionLevel
    ): List<BuildPromotionInfoItem<*>> {
        // Items
        val items = mutableListOf<BuildPromotionInfoItem<*>>()
        // Getting the eligible slots which have a rule on this promotion
        val eligibleSlots = slotService.getEligibleSlotsForBuild(build)
            .filter { it.eligible }
            .map { it.slot }
            .filter {
                isSlotForPromotionLevel(it, promotionLevel)
            }
            .sortedByDescending { it.environment.order }
        items += eligibleSlots.map {
            buildPromotionInfoItemForEligibleSlot(it)
        }
        // Getting the pipelines whose slot has a rule on this promotion
        val pipelines = slotService.findPipelineByBuild(build)
            .filter {
                isSlotForPromotionLevel(it.slot, promotionLevel)
            }
            .sortedWith(
                compareByDescending<SlotPipeline> { it.slot.environment.order }
                    .thenByDescending { it.number }
            )
        items += pipelines.map {
            buildPromotionInfoItemForSlotPipeline(it)
        }
        // OK
        return items
    }

    private fun isSlotForPromotionLevel(
        slot: Slot,
        promotionLevel: PromotionLevel
    ): Boolean {
        return slotService.getAdmissionRuleConfigs(slot)
            .count { config ->
                val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
                isAdmissionRuleConfigForPromotionLevel(rule, config, promotionLevel)
            } == 1
    }

    private fun isSlotForNoPromotionLevel(
        slot: Slot,
    ): Boolean {
        return slotService.getAdmissionRuleConfigs(slot)
            .none { config ->
                val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
                rule is PromotionRelatedSlotAdmissionRule
            }
    }

    private fun <T, D> isAdmissionRuleConfigForPromotionLevel(
        rule: SlotAdmissionRule<T, D>,
        config: SlotAdmissionRuleConfig,
        promotionLevel: PromotionLevel
    ): Boolean {
        if (rule is PromotionRelatedSlotAdmissionRule) {
            val ruleConfig = rule.parseConfig(config.ruleConfig)
            return rule.isForPromotionLevel(ruleConfig, promotionLevel)
        } else {
            return false
        }
    }

    private fun buildPromotionInfoItemForEligibleSlot(slot: Slot) =
        BuildPromotionInfoItem(
            type = BUILD_PROMOTION_INFO_ELIGIBLE_SLOT,
            data = slot,
        )

    private fun buildPromotionInfoItemForSlotPipeline(slotPipeline: SlotPipeline) =
        BuildPromotionInfoItem(
            type = BUILD_PROMOTION_INFO_SLOT_PIPELINE,
            data = slotPipeline,
        )
}