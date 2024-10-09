package net.nemerosa.ontrack.extensions.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule
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
) : SlotAdmissionRule<ValidationSlotAdmissionRuleConfig> {

    companion object {
        const val ID = "validation"
    }

    override val id: String = ID
    override val name: String = "Validation"

    /**
     * Getting the last branches having the configured validation
     * and getting their last builds.
     */
    override fun getEligibleBuilds(
        slot: Slot,
        config: ValidationSlotAdmissionRuleConfig,
        size: Int
    ): List<Build> {
        val branches = validationStampService.findBranchesWithValidationStamp(slot.project, config.validation, size)
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

    override fun parseConfig(jsonRuleConfig: JsonNode): ValidationSlotAdmissionRuleConfig = jsonRuleConfig.parse()

    /**
     * Build eligible if its branch has the validation required by the rule.
     */
    override fun isBuildEligible(build: Build, slot: Slot, config: ValidationSlotAdmissionRuleConfig): Boolean =
        structureService.getValidationStampListForBranch(build.branch.id).any { it.name == config.validation }

    override fun isBuildDeployable(build: Build, slot: Slot, config: ValidationSlotAdmissionRuleConfig): Boolean {
        val vs = structureService.findValidationStampByName(
            build.project.name,
            build.branch.name,
            config.validation
        ).getOrNull() ?: return false
        return structureService.getValidationRunsForBuildAndValidationStamp(
            build = build,
            validationStamp = vs,
            statuses = validationRunStatusService.validationRunStatusList.filter { it.isPassed }.map { it.id }
        ).isNotEmpty()
    }

    override fun getConfigName(config: ValidationSlotAdmissionRuleConfig): String {
        TODO("Not yet implemented")
    }
}