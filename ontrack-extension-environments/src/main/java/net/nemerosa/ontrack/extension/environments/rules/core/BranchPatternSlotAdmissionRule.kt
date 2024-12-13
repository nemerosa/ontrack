package net.nemerosa.ontrack.extension.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.FilterHelper
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class BranchPatternSlotAdmissionRule(

) : SlotAdmissionRule<BranchPatternSlotAdmissionRuleConfig, Any> {

    companion object {
        const val ID = "branchPattern"
    }

    override val id: String = ID
    override val name: String = "Branch pattern"

    override fun parseConfig(jsonRuleConfig: JsonNode) = jsonRuleConfig.parse<BranchPatternSlotAdmissionRuleConfig>()

    override fun isBuildEligible(build: Build, slot: Slot, config: BranchPatternSlotAdmissionRuleConfig): Boolean =
        FilterHelper.includes(
            text = build.branch.name,
            includes = config.includes,
            excludes = config.excludes,
        )

    override fun fillEligibilityCriteria(
        slot: Slot,
        config: BranchPatternSlotAdmissionRuleConfig,
        queries: MutableList<String>,
        params: MutableMap<String, Any?>,
        deployable: Boolean,
    ) {
        if (config.includes.isEmpty()) {
            queries += "0 != 1" // No inclusion, no build
        } else {
            config.includes.forEachIndexed { index, s ->
                queries += "B.NAME ~ :include$index"
                params["include$index"] = s
            }
            config.excludes.forEachIndexed { index, s ->
                queries += "B.NAME !~ :exclude$index"
                params["exclude$index"] = s
            }
        }
    }

    override fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: BranchPatternSlotAdmissionRuleConfig,
        ruleData: SlotPipelineAdmissionRuleData<Any>?
    ): DeployableCheck =
        DeployableCheck.check(
            check = isBuildEligible(
                build = pipeline.build,
                config = ruleConfig,
                slot = pipeline.slot,
            ),
            ok = "Build branch is valid",
            nok = "Build branch is not valid",
        )

    override fun parseData(node: JsonNode): Any = ""

    override fun checkConfig(ruleConfig: JsonNode) {
        ruleConfig.parseOrNull<BranchPatternSlotAdmissionRuleConfig>()
            ?: throw SlotAdmissionRuleConfigException("Cannot parse the rule config")
    }
}