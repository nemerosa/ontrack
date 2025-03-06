package net.nemerosa.ontrack.extension.environments.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
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
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.json.schema.toType
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

    override fun jsonType(jsonTypeBuilder: JsonTypeBuilder): JsonType =
        jsonTypeBuilder.toType(EnvironmentsCascModel::class)

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
            val qualifier = slotCasc.qualifier
            val project = structureService.findProjectByName(slotCasc.project).getOrNull()
            if (project != null) {
                val existingSlots = slotService.findSlotsByProject(project, qualifier)
                syncForward(
                    from = slotCasc.environments,
                    to = existingSlots
                ) {
                    equality { a, b ->
                        a.name == b.environment.name
                    }
                    onCreation { a ->
                        val environment = environmentService.findByName(a.name)
                        if (environment != null) {
                            logger.info("[casc][slot] Creating slot ${project}[$qualifier] -> ${a.name}")
                            val slot = Slot(
                                environment = environment,
                                project = project,
                                qualifier = slotCasc.qualifier,
                                description = slotCasc.description,
                            ).apply {
                                slotService.addSlot(this)
                            }
                            runSlotAdmissionRules(slot, a)
                            runSlotWorkflows(slot, a)
                        } else {
                            logger.warn("[casc][slot] Slot ${project}[$qualifier] environment not found -> ${a.name}")
                        }
                    }
                    onModification { a, existing ->
                        logger.info("[casc][slot] Existing slot ${project}[$qualifier] -> ${a.name}")
                        runSlotAdmissionRules(existing, a)
                        runSlotWorkflows(existing, a)
                    }
                    onDeletion { existing ->
                        logger.info("[casc][slot] Deleting slot ${project}[$qualifier] -> ${existing.environment.name}")
                        slotService.deleteSlot(existing)
                    }
                }
            } else {
                logger.warn("[casc][slot] Project ${slotCasc.project} does not exist")
            }
        }
    }

    private fun runSlotWorkflows(slot: Slot, slotEnvironmentCasc: SlotEnvironmentCasc) {
        val cascWorkflows = slotEnvironmentCasc.workflows
        val existingWorkflows = slotWorkflowService.getSlotWorkflowsBySlot(slot)
        syncForward(
            from = cascWorkflows,
            to = existingWorkflows
        ) {
            equality { a, b ->
                a.name == b.workflow.name
            }
            onCreation { a ->
                val newSlotWorkflow = SlotWorkflow(
                    slot = slot,
                    trigger = a.trigger,
                    workflow = Workflow(
                        name = a.name,
                        nodes = a.nodes,
                    )
                )
                logger.info("[casc][slot-workflow] Creating slot workflow ${slot.project.name}[${slot.qualifier}] -> ${slot.environment.name}: ${a.name} [${newSlotWorkflow.id}]")
                slotWorkflowService.addSlotWorkflow(
                    newSlotWorkflow
                )
            }
            onModification { a, existing ->
                logger.info("[casc][slot-workflow] Updating slot workflow ${slot.project.name}[${slot.qualifier}] -> ${slot.environment.name}: ${a.name} [${existing.id}]")
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
                logger.info("[casc][slot-workflow] Deleting slot workflow ${slot.project.name}[${slot.qualifier}] -> ${slot.environment.name}: ${existing.workflow.name} [${existing.id}]")
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
                logger.info("[casc][slot-workflow] Creating slot rule ${slot.project.name}[${slot.qualifier}] -> ${slot.environment.name}: ${a.actualName}")
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
                logger.info("[casc][slot-workflow] Updating slot rule ${slot.project.name}[${slot.qualifier}] -> ${slot.environment.name}: ${a.actualName}")
                slotService.saveAdmissionRuleConfig(
                    existing
                        .withDescription(a.description)
                        .withRuleConfig(a.ruleConfig)
                )
            }
            onDeletion { a ->
                logger.info("[casc][slot-workflow] Deleting slot rule ${slot.project.name}[${slot.qualifier}] -> ${slot.environment.name}: ${a.name}")
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
                logger.info("[casc][environment] Creating ${env.name}")
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
                logger.info("[casc][environment] Updating ${env.name}")
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
                    logger.info("[casc][environment] Deleting ${existing.name}")
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