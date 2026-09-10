package net.nemerosa.ontrack.extension.environments.deliverymap

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowInstance
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

/**
 * Draws the workflows configured on each slot of the project, and joins them to their slot.
 *
 * Two of the three triggers are **hard gates**, unconditionally and with no admission rule
 * involved: a `CANDIDATE` workflow which is not ok stops the deployment from starting, and a
 * `RUNNING` one stops it from finishing (`SlotServiceImpl`). They are therefore prerequisites, and
 * are drawn with the map's existing *requires*, running workflow to slot exactly as an admission
 * rule does. `DONE` gates nothing - it runs once the deployment is over - so it is drawn with
 * *emits*, running slot to workflow. A slot carrying workflows on several triggers straddles its own
 * column, its gates to the left and its consequences to the right.
 *
 * **A workflow which has never run is still drawn, and so is its edge.** That matters most for the
 * gates: a `CANDIDATE` workflow nothing ever ran is not dormant, it is the reason nothing has ever
 * deployed to that slot, and saying so is the most useful thing the map can offer about it.
 *
 * The run each workflow shows comes from the slot's CURRENT pipeline, falling back to the last
 * deployed one. The slot checkpoint's own arrival keeps using the last deployed pipeline alone - the
 * map must never claim a build has arrived somewhere it has not - but a workflow is not an arrival,
 * and reading only finished deployments would leave a `CANDIDATE` workflow able to show itself only
 * after it had already let something through.
 *
 * Unreachability is the slot checkpoint's own statement and is deliberately not repeated here: what
 * these nodes say is which gates the slot has and what the latest pipeline made of them, which is
 * true of the slot whichever branch is reading it.
 *
 * This is a contributor of its own, beside [SlotDeliveryMapContributor] rather than inside it, so
 * that a failure reading a workflow cannot take the project's slots off the map with it -
 * `DeliveryMapServiceImpl` isolates per contributor.
 */
@Component
class SlotWorkflowDeliveryMapContributor(
    private val slotService: SlotService,
    private val slotWorkflowService: SlotWorkflowService,
) : DeliveryMapContributor {

    override fun contribute(branch: Branch): DeliveryMapContribution {
        // Already narrowed to the slots the current user may see, which is what a contributor owes
        // the map: no placeholder for a permission denial
        val slots = slotService.findSlotsByProject(branch.project)
            .sortedBy { it.environment.order }
        if (slots.isEmpty()) return DeliveryMapContribution.EMPTY

        val checkpoints = mutableListOf<DeliveryMapCheckpoint>()
        val edges = mutableListOf<DeliveryMapEdge>()

        slots.forEach { slot ->
            val slotWorkflows = slotWorkflowService.getSlotWorkflowsBySlot(slot)
            if (slotWorkflows.isEmpty()) return@forEach

            val instances = instancesByWorkflowId(slot)
            val slotId = SlotDeliveryMapCheckpoints.slot(slot.id)

            slotWorkflows.forEach { slotWorkflow ->
                val checkpoint = checkpoint(slotWorkflow, instances[slotWorkflow.id])
                checkpoints += checkpoint
                edges += edge(slotWorkflow.trigger, workflowId = checkpoint.id, slotId = slotId)
            }
        }

        return DeliveryMapContribution(checkpoints = checkpoints, edges = edges)
    }

    /**
     * What the slot's workflows did on the pipeline worth reading, keyed by configured workflow.
     *
     * The current pipeline first, so that a gate which is holding a deployment up right now is what
     * the map shows; the last deployed one when there is nothing in flight.
     */
    private fun instancesByWorkflowId(slot: Slot): Map<String, SlotWorkflowInstance> {
        val pipeline = slotService.getCurrentPipeline(slot)
            ?: slotService.getLastDeployedPipeline(slot)
            ?: return emptyMap()
        return slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline)
            .associateBy { it.slotWorkflow.id }
    }

    /**
     * Which way the edge runs, and what it means, is decided by the trigger alone.
     */
    private fun edge(trigger: SlotPipelineStatus, workflowId: String, slotId: String) =
        when (trigger) {
            // A gate: the deployment cannot start, or cannot finish, until this passes
            SlotPipelineStatus.CANDIDATE, SlotPipelineStatus.RUNNING ->
                DeliveryMapEdge.of(DeliveryMapEdgeKind.REQUIRES, source = workflowId, target = slotId)
            // A consequence: it runs once the deployment is over and nothing waits for it
            else ->
                DeliveryMapEdge.of(DeliveryMapEdgeKind.EMITS, source = slotId, target = workflowId)
        }

    private fun checkpoint(slotWorkflow: SlotWorkflow, instance: SlotWorkflowInstance?) =
        DeliveryMapCheckpoint(
            id = SlotDeliveryMapCheckpoints.slotWorkflow(slotWorkflow.id),
            type = SlotDeliveryMapCheckpoints.SLOT_WORKFLOW,
            name = slotWorkflow.workflow.name,
            data = SlotWorkflowCheckpointData(
                slotWorkflowId = slotWorkflow.id,
                trigger = slotWorkflow.trigger,
                workflowInstanceId = instance?.workflowInstance?.id,
                status = instance?.workflowInstance?.status,
                startTime = instance?.workflowInstance?.startTime,
                durationMs = instance?.workflowInstance?.durationMs,
            ).asJson(),
            // No arrival: the build would always be the one the slot beside it already names
        )

}
