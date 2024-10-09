package net.nemerosa.ontrack.extensions.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevelService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class PromotionSlotAdmissionRule(
    private val structureService: StructureService,
    private val promotionLevelService: PromotionLevelService,
    private val buildFilterService: BuildFilterService,
) : SlotAdmissionRule<PromotionSlotAdmissionRuleConfig> {

    companion object {
        const val ID = "promotion"
    }

    override val id: String = ID
    override val name: String = "Promotion"

    /**
     * Getting the last branches having the configured promotion
     * and getting their last builds.
     */
    override fun getEligibleBuilds(
        slot: Slot,
        config: PromotionSlotAdmissionRuleConfig,
        size: Int
    ): List<Build> {
        val branches = promotionLevelService.findBranchesWithPromotionLevel(slot.project, config.promotion, size)
        return branches.asSequence()
            .flatMap { branch ->
                buildFilterService.standardFilterProviderData(size)
                    .build()
                    .filterBranchBuilds(branch)
                    .asSequence()
            }
            .take(size)
            .toList()
    }

    override fun parseConfig(jsonRuleConfig: JsonNode): PromotionSlotAdmissionRuleConfig = jsonRuleConfig.parse()

    /**
     * Build eligible if its branch has the promotion required by the rule.
     */
    override fun isBuildEligible(build: Build, slot: Slot, config: PromotionSlotAdmissionRuleConfig): Boolean =
        structureService.getPromotionLevelListForBranch(build.branch.id).any { it.name == config.promotion }

    override fun isBuildDeployable(build: Build, slot: Slot, config: PromotionSlotAdmissionRuleConfig): Boolean {
        val pl = structureService.findPromotionLevelByName(
            build.project.name,
            build.branch.name,
            config.promotion
        ).getOrNull() ?: return false
        return structureService.getLastPromotionRunForBuildAndPromotionLevel(build, pl).getOrNull() != null
    }

    override fun getConfigName(config: PromotionSlotAdmissionRuleConfig): String {
        TODO("Not yet implemented")
    }
}