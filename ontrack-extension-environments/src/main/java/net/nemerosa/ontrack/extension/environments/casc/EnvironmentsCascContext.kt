package net.nemerosa.ontrack.extension.environments.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.files.FileRef
import net.nemerosa.ontrack.model.files.FileRefService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ImageHelper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.optionals.getOrNull

@Component
class EnvironmentsCascContext(
    private val environmentService: EnvironmentService,
    private val structureService: StructureService,
    private val slotService: SlotService,
    private val slotWorkflowService: SlotWorkflowService,
    private val fileRefService: FileRefService,
) : AbstractCascContext(), SubConfigContext {

    private val logger = LoggerFactory.getLogger(EnvironmentsCascContext::class.java)

    override val field: String = "environments"

    override val type: CascType = cascObject(EnvironmentsCascModel::class)

    override fun run(node: JsonNode, paths: List<String>) {
        val model: EnvironmentsCascModel = node.parse()
        run(model)
    }

    private fun run(model: EnvironmentsCascModel) {
        runEnvironments(model)
        runSlots(model.slots)
    }

    private fun runSlots(slots: List<SlotCasc>) {
        slots.forEach { slotCasc ->
            val project = structureService.findProjectByName(slotCasc.project).getOrNull()
            if (project != null) {
                slotCasc.environments.forEach { slotEnvironmentCasc ->
                    val environment = environmentService.findByName(slotEnvironmentCasc.name)
                    if (environment != null) {
                        val existing = slotService.findSlotByProjectAndEnvironment(
                            environment = environment,
                            project = project,
                            qualifier = slotCasc.qualifier,
                        )
                        val slot = if (existing != null) {
                            logger.info("Slot $slotCasc for environment ${slotEnvironmentCasc.name} already exists.")
                            existing
                        } else {
                            logger.info("Creating $slotCasc for environment ${slotEnvironmentCasc.name}")
                            Slot(
                                environment = environment,
                                project = project,
                                qualifier = slotCasc.qualifier,
                                description = slotCasc.description,
                            ).apply {
                                slotService.addSlot(this)
                            }
                        }
                        runSlotAdmissionRules(slot, slotEnvironmentCasc)
                        runSlotWorkflows(slot, slotEnvironmentCasc)
                    } else {
                        logger.warn("Environment ${slotEnvironmentCasc.name} does not exist")
                    }
                }
            } else {
                logger.warn("Project ${slotCasc.project} does not exist")
            }
        }
    }

    private fun runSlotWorkflows(slot: Slot, slotEnvironmentCasc: SlotEnvironmentCasc) {
        val existingWorkflows = slotWorkflowService.getSlotWorkflowsBySlot(slot)
        val cascWorkflows = slotEnvironmentCasc.workflows
        syncForward(
            from = cascWorkflows,
            to = existingWorkflows
        ) {
            equality { a, b ->
                a.name == b.workflow.name
            }
            onCreation { a ->
                slotWorkflowService.addSlotWorkflow(
                    SlotWorkflow(
                        slot = slot,
                        trigger = a.trigger,
                        workflow = Workflow(
                            name = a.name,
                            nodes = a.nodes,
                        )
                    )
                )
            }
            onModification { a, existing ->
                slotWorkflowService.updateSlotWorkflow(
                    existing
                        .withTrigger(a.trigger)
                        .withWorkflow(
                            Workflow(
                                name = a.name,
                                nodes = a.nodes
                            )
                        )
                )
            }
            onDeletion { existing ->
                slotWorkflowService.deleteSlotWorkflow(existing)
            }
        }
    }

    private fun runSlotAdmissionRules(slot: Slot, slotEnvironmentCasc: SlotEnvironmentCasc) {
        val existingRules = slotService.getAdmissionRuleConfigs(slot)
        syncForward(
            from = slotEnvironmentCasc.admissionRules,
            to = existingRules,
        ) {
            equality { a, b ->
                a.actualName == b.name
            }
            onCreation { a ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleConfig(
                        slot = slot,
                        name = a.actualName,
                        description = a.description,
                        ruleId = a.ruleId,
                        ruleConfig = a.ruleConfig,
                    )
                )
            }
            onModification { a, existing ->
                slotService.saveAdmissionRuleConfig(
                    existing
                        .withDescription(a.description)
                        .withRuleConfig(a.ruleConfig)
                )
            }
            onDeletion { a ->
                slotService.deleteAdmissionRuleConfig(a)
            }
        }
    }

    private fun runEnvironments(model: EnvironmentsCascModel) {
        val existingEnvs = environmentService.findAll()
        syncForward(
            from = model.environments,
            to = existingEnvs,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { env ->
                val environment = Environment(
                    name = env.name,
                    description = env.description,
                    order = env.order,
                    tags = env.tags,
                    image = false,
                )
                environmentService.save(environment)
                setImage(env, environment)
            }
            onModification { env, existing ->
                val adapted = Environment(
                    id = existing.id,
                    name = existing.name,
                    description = env.description,
                    order = env.order,
                    tags = env.tags,
                    image = existing.image,
                )
                environmentService.save(adapted)
                setImage(env, adapted)
            }
            onDeletion { existing ->
                if (!model.keepEnvironments) {
                    environmentService.delete(existing)
                }
            }
        }
    }

    private fun setImage(
        env: EnvironmentCasc,
        environment: Environment
    ) {
        if (!env.image.isNullOrBlank()) {
            val image = fileRefService.downloadDocument(
                ref = FileRef.parseUri(env.image) ?: error("Cannot parse image URI: ${env.image}"),
                type = ImageHelper.IMAGE_PNG,
            ) ?: error("Cannot download image at ${env.image}")
            environmentService.setEnvironmentImage(
                id = environment.id,
                document = image
            )
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun render(): JsonNode {
        val environments = environmentService.findAll()
        return EnvironmentsCascModel(
            keepEnvironments = true, // Always true when rendering
            environments = environments.map {
                EnvironmentCasc(
                    name = it.name,
                    description = it.description ?: "",
                    order = it.order,
                    tags = it.tags,
                    image = environmentService.getEnvironmentImage(it.id)
                        .takeIf { doc -> !doc.isEmpty }
                        ?.content
                        ?.let { bytes -> Base64.encode(bytes) }
                        ?.let { base64 -> "base64:$base64" },
                )
            },
            slots = environments.flatMap { environment ->
                slotService.findSlotsByEnvironment(environment)
            }.groupBy { it.project.name to it.qualifier }.map { (slotKey, slots) ->
                val (projectName, qualifier) = slotKey
                SlotCasc(
                    project = projectName,
                    qualifier = qualifier,
                    description = slots.first().description ?: "",
                    environments = slots.map { slot ->
                        SlotEnvironmentCasc(
                            name = slot.environment.name,
                            admissionRules = slotService.getAdmissionRuleConfigs(slot).map { rule ->
                                SlotEnvironmentAdmissionRuleCasc(
                                    name = rule.name,
                                    description = rule.description ?: "",
                                    ruleId = rule.ruleId,
                                    ruleConfig = rule.ruleConfig,
                                )
                            },
                            workflows = slotWorkflowService.getSlotWorkflowsBySlot(slot).map { sw ->
                                SlotWorkflowCasc(
                                    trigger = sw.trigger,
                                    name = sw.workflow.name,
                                    nodes = sw.workflow.nodes,
                                )
                            }
                        )
                    }
                )
            },
        ).asJson()
    }

}