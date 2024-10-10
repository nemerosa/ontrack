package net.nemerosa.ontrack.extensions.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.*
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.BranchFilter
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class ManualApprovalSlotAdmissionRule(
    private val structureService: StructureService,
    private val buildFilterService: BuildFilterService,
    private val securityService: SecurityService,
    private val accountService: AccountService,
) : SlotAdmissionRule<ManualApprovalSlotAdmissionRuleConfig, ManualApprovalSlotAdmissionRuleData> {

    companion object {
        const val ID = "manual"
    }

    override val id: String = ID
    override val name: String = "Manual approval"

    override fun parseConfig(jsonRuleConfig: JsonNode): ManualApprovalSlotAdmissionRuleConfig = jsonRuleConfig.parse()

    /**
     * Getting the last N builds of a project for enabled branches
     */
    override fun getEligibleBuilds(slot: Slot, config: ManualApprovalSlotAdmissionRuleConfig, size: Int): List<Build> {
        val branches = structureService.filterBranchesForProject(
            slot.project,
            BranchFilter(count = size, enabled = true)
        )
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

    /**
     * All builds are eligible
     */
    override fun isBuildEligible(build: Build, slot: Slot, config: ManualApprovalSlotAdmissionRuleConfig): Boolean =
        true

    /**
     * A build is deployable if the user has approved the rule in the pipeline.
     *
     * Only some users or groups are eligible.
     */
    override fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: ManualApprovalSlotAdmissionRuleConfig,
        ruleData: SlotPipelineAdmissionRuleData<ManualApprovalSlotAdmissionRuleData>?
    ): DeployableCheck {
        return if (ruleData?.data != null) {
            if (!ruleData.data.approval) {
                DeployableCheck.nok("Rejected")
            } else {
                DeployableCheck.ok()
            }
        } else {
            DeployableCheck.nok("No approval")
        }
    }

    override fun getConfigName(config: ManualApprovalSlotAdmissionRuleConfig): String {
        TODO("Not yet implemented")
    }

    override fun parseData(node: JsonNode): ManualApprovalSlotAdmissionRuleData =
        node.parse()

    override fun checkData(ruleConfig: JsonNode, data: JsonNode) {
        val c = parseConfig(ruleConfig)
        val d = parseData(data)
        // Controls of the user
        val user = securityService.currentSignature.user.name
        if (c.users.isNotEmpty()) {
            if (user !in c.users) {
                throw ManualApprovalSlotAdmissionRuleException("User not authorized to approve")
            }
        }
        // Controls of the group
        if (c.groups.isNotEmpty()) {
            val groups = securityService.currentAccount?.accountGroups?.map { it.name } ?: emptyList()
            if (c.groups.intersect(groups).isEmpty()) {
                throw ManualApprovalSlotAdmissionRuleException("Group not authorized to approve")
            }
        }
        // OK
    }
}