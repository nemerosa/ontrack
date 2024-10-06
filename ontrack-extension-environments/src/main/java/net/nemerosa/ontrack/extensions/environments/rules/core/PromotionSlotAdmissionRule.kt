package net.nemerosa.ontrack.extensions.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class PromotionSlotAdmissionRule(
    private val structureService: StructureService,
) : SlotAdmissionRule<PromotionSlotAdmissionRuleConfig> {

    companion object {
        const val ID = "promotion"
    }

    override val id: String = ID
    override val name: String = "Promotion"

    override fun getEligibleBuilds(
        slot: Slot,
        config: PromotionSlotAdmissionRuleConfig,
        offset: Int,
        size: Int
    ): List<Build> {
        TODO("Not yet implemented")
    }

    override fun parseConfig(jsonRuleConfig: JsonNode): PromotionSlotAdmissionRuleConfig = jsonRuleConfig.parse()

    /**
     * Build eligible if its branch has the promotion required by the rule.
     */
    override fun isBuildEligible(build: Build, slot: Slot, config: PromotionSlotAdmissionRuleConfig): Boolean =
        structureService.getPromotionLevelListForBranch(build.branch.id).any { it.name == config.promotion }

    override fun isBuildDeployable(build: Build, slot: Slot, config: PromotionSlotAdmissionRuleConfig): Boolean {
        TODO("Not yet implemented")
    }

    override fun getConfigName(config: PromotionSlotAdmissionRuleConfig): String {
        TODO("Not yet implemented")
    }
}