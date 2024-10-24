package net.nemerosa.ontrack.extension.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class EnvironmentSlotAdmissionRule(
    private val environmentService: EnvironmentService,
    private val applicationContext: ApplicationContext,
) : SlotAdmissionRule<EnvironmentSlotAdmissionRuleConfig, Any> {

    companion object {
        const val ID = "environment"
    }

    /**
     * Cannot inject the [SlotService] directly since it would create
     * a circular dependency.
     */
    private val slotService: SlotService by lazy {
        applicationContext.getBean(SlotService::class.java)
    }

    override val id: String = ID
    override val name: String = "Deployed in environment slot"

    override fun parseConfig(jsonRuleConfig: JsonNode): EnvironmentSlotAdmissionRuleConfig =
        jsonRuleConfig.parse()

    override fun parseData(node: JsonNode): Any = ""

    override fun checkConfig(ruleConfig: JsonNode) {
        ruleConfig.parseOrNull<EnvironmentSlotAdmissionRuleConfig>()
            ?: throw SlotAdmissionRuleConfigException("Cannot parse the rule config")
    }

    /**
     * We need to check that the build project has a slot (and qualifier) on the configured environment.
     */
    override fun isBuildEligible(build: Build, slot: Slot, config: EnvironmentSlotAdmissionRuleConfig): Boolean {
        if (build.project.id() != slot.project.id()) return false
        return findPreviousSlot(build, config) != null
    }

    private fun findPreviousSlot(
        build: Build,
        config: EnvironmentSlotAdmissionRuleConfig
    ): Slot? =
        environmentService.findByName(config.environmentName)?.let { environment ->
            slotService.findSlotsByEnvironment(environment).firstOrNull {
                it.project.id() == build.project.id() && it.qualifier == config.qualifier
            }
        }

    /**
     * Build is deployable if the corresponding slot in the configured environment (and qualifier)
     * has its current pipeline in "deployed" state for this build.
     */
    override fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: EnvironmentSlotAdmissionRuleConfig,
        ruleData: SlotPipelineAdmissionRuleData<Any>?
    ): DeployableCheck {
        // Gets the previous slot
        val previousSlot = findPreviousSlot(pipeline.build, ruleConfig)
            ?: return DeployableCheck.nok("""Cannot find previous slot for project = "${pipeline.build.project.name}", environment = ${ruleConfig.environmentName} and qualifier = "${ruleConfig.qualifier}".""")
        // Gets its last pipeline
        val previousPipeline = slotService.getCurrentPipeline(previousSlot)
            ?: return DeployableCheck.nok("""Slot for project = "${pipeline.build.project.name}", environment = ${ruleConfig.environmentName} and qualifier = "${ruleConfig.qualifier}" has no pipeline.""")
        // Checks it's for the same build
        if (previousPipeline.build.id() != pipeline.build.id()) {
            return DeployableCheck.nok("""Pipeline for project = "${pipeline.build.project.name}", environment = ${ruleConfig.environmentName} and qualifier = "${ruleConfig.qualifier}" is for another build.""")
        }
        // Checks it's deployed
        if (previousPipeline.status != SlotPipelineStatus.DEPLOYED) {
            return DeployableCheck.nok("""Build is in a pipeline for project = "${pipeline.build.project.name}", environment = ${ruleConfig.environmentName} and qualifier = "${ruleConfig.qualifier}" but this pipeline has not been deployed.""")
        }
        // OK
        return DeployableCheck.ok()
    }

    /**
     * Adding to the criteria the fact that there must exist a slot in the previous environment
     * for the same qualifier & project.
     */
    override fun fillEligibilityCriteria(
        slot: Slot,
        config: EnvironmentSlotAdmissionRuleConfig,
        queries: MutableList<String>,
        params: MutableMap<String, Any?>
    ) {
        queries += """
            EXISTS (
                SELECT S.ID
                FROM ENV_SLOTS S
                INNER JOIN ENVIRONMENTS E ON E.ID = S.ENVIRONMENT_ID
                WHERE E.NAME = :environmentName
                AND S.PROJECT_ID = :projectId
                AND S.QUALIFIER = :qualifier
                LIMIT 1
            )
        """
        params["environmentName"] = config.environmentName
        params["qualifier"] = config.qualifier
        params["projectId"] = slot.project.id()
    }
}