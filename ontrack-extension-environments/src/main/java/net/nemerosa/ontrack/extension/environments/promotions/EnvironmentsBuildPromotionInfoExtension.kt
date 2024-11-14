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
import kotlin.reflect.KClass

@Component
class EnvironmentsBuildPromotionInfoExtension(
    extensionFeature: EnvironmentsExtensionFeature,
    private val slotService: SlotService,
    private val slotAdmissionRuleRegistry: SlotAdmissionRuleRegistry,
) : AbstractExtension(extensionFeature), BuildPromotionInfoExtension {

    override val types: Collection<KClass<*>> = setOf(
        Slot::class,
        SlotPipeline::class,
    )

    override fun buildPromotionInfoItems(
        items: MutableList<BuildPromotionInfoItem<*>>,
        build: Build,
        promotionLevels: List<PromotionLevel>
    ) {
        val contributions = mutableListOf<BuildPromotionInfoItem<*>>()
        // Pipelines for this build
        val buildPipelines = slotService.findPipelineByBuild(build)
        // Gets the eligible slots for this build
        val eligibleSlots = slotService.getEligibleSlotsForBuild(build)
            .filter { it.eligible }
            .map { it.slot }
            .sortedByDescending { it.environment.order }
        eligibleSlots.forEach { slot ->
            // Getting the promotion level for this slot
            val promotionLevel = promotionLevels.firstOrNull { pl ->
                isSlotForPromotionLevel(slot, pl)
            }
            // Adding the contribution for this slot
            contributions += buildPromotionInfoItemForEligibleSlot(slot, promotionLevel)
            // Getting the pipelines for this slot & build
            val pipelines = buildPipelines.filter { it.slot.id == slot.id }
                .sortedByDescending { it.number }
            pipelines.forEach { pipeline ->
                contributions += buildPromotionInfoItemForSlotPipeline(pipeline, promotionLevel)
            }
        }
        // Add the contributions before all the other items
        items.addAll(0, contributions)
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

    private fun buildPromotionInfoItemForEligibleSlot(slot: Slot, promotionLevel: PromotionLevel?) =
        BuildPromotionInfoItem(
            promotionLevel = promotionLevel,
            data = slot,
        )

    private fun buildPromotionInfoItemForSlotPipeline(slotPipeline: SlotPipeline, promotionLevel: PromotionLevel?) =
        BuildPromotionInfoItem(
            promotionLevel = promotionLevel,
            data = slotPipeline,
        )
}