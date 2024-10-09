package net.nemerosa.ontrack.extensions.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.*
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
) : SlotAdmissionRule<PromotionSlotAdmissionRuleConfig, Any> {

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

    override fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: PromotionSlotAdmissionRuleConfig,
        ruleData: SlotPipelineAdmissionRuleData<Any>?
    ): DeployableCheck {
        val pl = structureService.findPromotionLevelByName(
            pipeline.build.project.name,
            pipeline.build.branch.name,
            ruleConfig.promotion
        ).getOrNull() ?: return DeployableCheck.nok("Promotion not existing")
        return DeployableCheck.check(
            structureService.getLastPromotionRunForBuildAndPromotionLevel(pipeline.build, pl).getOrNull() != null,
            "Build promoted",
            "Build not promoted"
        )
    }

    override fun getConfigName(config: PromotionSlotAdmissionRuleConfig): String {
        TODO("Not yet implemented")
    }

    override fun parseData(node: JsonNode): Any = ""

}