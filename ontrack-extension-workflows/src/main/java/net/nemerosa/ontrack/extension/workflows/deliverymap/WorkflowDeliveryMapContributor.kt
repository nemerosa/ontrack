package net.nemerosa.ontrack.extension.workflows.deliverymap

import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.notifications.EntityWorkflowInstanceService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import org.springframework.stereotype.Component

/**
 * Draws the workflows a promotion set off, as checkpoints of their own hanging off their promotion
 * level.
 *
 * They are *emitted*, never required: a workflow subscribed to `NEW_PROMOTION_RUN` runs after the
 * promotion is granted and nothing waits for its result. Drawing it as a prerequisite would say the
 * promotion is gated by it, which is exactly the mistake the third edge kind exists to avoid - see
 * ADR 0011.
 *
 * **The workflows of the arrival's run only.** The promotion level checkpoint names one build and
 * one run; drawing the workflows of every run the level ever had would put runs of different builds
 * on one node with nothing saying which is which. That run is also named in the promotion level's
 * own payload ([PromotionLevelCheckpointData.promotionRunId]), so the two halves of the map can be
 * read as talking about one promotion.
 *
 * **A promotion level never promoted has no workflow checkpoints at all**, even when subscriptions
 * exist. A workflow is only reachable through the notification records of the runs which fired it,
 * so there is nothing to draw before the first promotion. That is the opposite of what the slot side
 * does - a slot's workflows are configuration and are drawn whether or not anything ran - and the
 * asymmetry is deliberate and documented.
 *
 * The lookup is batched across the whole branch: [EntityWorkflowInstanceService] resolves every
 * promotion run at once, so this contributor costs one record query and one instance query however
 * many promotion levels the branch has. The per-level `getLastPromotionRunForPromotionLevel` calls
 * are the same ones the core's promotion level contributor already makes, on an indexed single-row
 * lookup.
 */
@Component
class WorkflowDeliveryMapContributor(
    private val structureService: StructureService,
    private val entityWorkflowInstanceService: EntityWorkflowInstanceService,
) : DeliveryMapContributor {

    override fun contribute(branch: Branch): DeliveryMapContribution {
        // The runs the promotion level checkpoints name, keyed by their entity id so that the
        // batched lookup below can be read back onto them
        val runsByEntity: Map<ProjectEntityID, PromotionRun> =
            structureService.getPromotionLevelListForBranch(branch.id)
                .mapNotNull { structureService.getLastPromotionRunForPromotionLevel(it) }
                .associateBy { it.toProjectEntityID() }
        if (runsByEntity.isEmpty()) return DeliveryMapContribution.EMPTY

        val instancesByEntity =
            entityWorkflowInstanceService.findWorkflowInstancesByEntities(runsByEntity.keys)
        if (instancesByEntity.isEmpty()) return DeliveryMapContribution.EMPTY

        val checkpoints = mutableListOf<DeliveryMapCheckpoint>()
        val edges = mutableListOf<DeliveryMapEdge>()

        instancesByEntity.forEach { (entity, instances) ->
            val run = runsByEntity[entity] ?: return@forEach
            val source = DeliveryMapCheckpointTypes.promotionLevel(run.promotionLevel.id)
            // Most recent first, so a name fired twice on one run keeps its latest run. Two
            // subscriptions whose workflows share a name collapse the same way, which is the
            // limitation the name-based id accepts.
            instances.distinctBy { it.workflow.name }.forEach { instance ->
                val checkpoint = checkpoint(run.promotionLevel.id(), instance)
                checkpoints += checkpoint
                edges += DeliveryMapEdge.of(
                    kind = DeliveryMapEdgeKind.EMITS,
                    source = source,
                    target = checkpoint.id,
                )
            }
        }

        return DeliveryMapContribution(checkpoints = checkpoints, edges = edges)
    }

    private fun checkpoint(promotionLevelId: Int, instance: WorkflowInstance) = DeliveryMapCheckpoint(
        id = WorkflowDeliveryMapCheckpoints.workflow(promotionLevelId, instance.workflow.name),
        type = WorkflowDeliveryMapCheckpoints.WORKFLOW,
        name = instance.workflow.name,
        data = WorkflowCheckpointData(
            workflowInstanceId = instance.id,
            status = instance.status,
            startTime = instance.startTime,
            durationMs = instance.durationMs,
        ).asJson(),
        // No arrival: the build would always be the one the promotion level already names
    )

}
