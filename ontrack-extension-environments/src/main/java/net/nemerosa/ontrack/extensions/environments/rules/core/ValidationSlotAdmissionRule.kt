package net.nemerosa.ontrack.extensions.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.*
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService
import net.nemerosa.ontrack.model.structure.ValidationStampService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class ValidationSlotAdmissionRule(
    private val structureService: StructureService,
    private val validationStampService: ValidationStampService,
    private val buildFilterService: BuildFilterService,
    private val validationRunStatusService: ValidationRunStatusService,
) : SlotAdmissionRule<ValidationSlotAdmissionRuleConfig, Any> {

    companion object {
        const val ID = "validation"
    }

    override val id: String = ID
    override val name: String = "Validation"

    /**
     * Getting builds which have passed
     */
    override fun fillEligibilityCriteria(
        slot: Slot,
        config: ValidationSlotAdmissionRuleConfig,
        queries: MutableList<String>,
        params: MutableMap<String, Any?>
    ) {
        TODO("Not yet implemented")
    }

    override fun parseConfig(jsonRuleConfig: JsonNode): ValidationSlotAdmissionRuleConfig = jsonRuleConfig.parse()

    /**
     * Build eligible if its branch has the validation required by the rule.
     */
    override fun isBuildEligible(build: Build, slot: Slot, config: ValidationSlotAdmissionRuleConfig): Boolean =
        structureService.getValidationStampListForBranch(build.branch.id).any { it.name == config.validation }

    override fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: ValidationSlotAdmissionRuleConfig,
        ruleData: SlotPipelineAdmissionRuleData<Any>?
    ): DeployableCheck {
        val vs = structureService.findValidationStampByName(
            pipeline.build.project.name,
            pipeline.build.branch.name,
            ruleConfig.validation
        ).getOrNull() ?: return DeployableCheck.nok("Validation not existing")
        return DeployableCheck.check(
            check = structureService.getValidationRunsForBuildAndValidationStamp(
                build = pipeline.build,
                validationStamp = vs,
                statuses = validationRunStatusService.validationRunStatusList.filter { it.isPassed }.map { it.id }
            ).isNotEmpty(),
            ok = "Build validated",
            nok = "Build not validated"
        )
    }

    override fun parseData(node: JsonNode): Any = ""
}