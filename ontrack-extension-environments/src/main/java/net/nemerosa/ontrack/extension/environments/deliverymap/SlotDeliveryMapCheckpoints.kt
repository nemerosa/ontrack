package net.nemerosa.ontrack.extension.environments.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStatus
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpointTypes
import java.time.LocalDateTime

/**
 * The checkpoint kinds the environments extension contributes to a delivery map.
 *
 * It lives here rather than in `DeliveryMapCheckpointTypes` because the set of checkpoint kinds is
 * open: the core names the kinds it contributes itself, and an extension names its own. The id is
 * still built through the core's [DeliveryMapCheckpointTypes.checkpointId], because checkpoint ids
 * share one namespace across every kind and that namespacing is the core's rule, not this one's.
 */
object SlotDeliveryMapCheckpoints {

    /**
     * A slot of the branch's project, arrived at by a deployment.
     */
    const val SLOT = "slot"

    /**
     * A slot's UUID is already unique; the type prefix is what keeps it apart from a promotion level
     * id or a validation stamp id in the shared namespace.
     */
    fun slot(slotId: String): String = DeliveryMapCheckpointTypes.checkpointId(SLOT, slotId)

    /**
     * A workflow configured on a slot, for one of the three moments of a deployment.
     *
     * A kind of its own rather than the workflows extension's `workflow` with a trigger added to its
     * payload: `checkpointTypes.js` fixes a node's height per KIND, before anything is rendered, and
     * a slot workflow draws the extra line naming its trigger. They also have different ids and
     * belong to different modules.
     */
    const val SLOT_WORKFLOW = "slot-workflow"

    /**
     * A slot workflow is identified by the CONFIGURATION it is, never by the run.
     *
     * `SlotWorkflow.id` is a persisted UUID which survives every pipeline, while
     * `SlotWorkflowInstance.id` is minted per run - and a checkpoint id which changed on every
     * deployment would change the map's topology with it, forcing a relayout and moving every node
     * the reader had placed (#1707).
     */
    fun slotWorkflow(slotWorkflowId: String): String =
        DeliveryMapCheckpointTypes.checkpointId(SLOT_WORKFLOW, slotWorkflowId)

    /**
     * How a slot is labelled on the map, from the environment it is in and its qualifier.
     *
     * The project is left out: it is what the map is about, and the environment and the qualifier are
     * what tell two slots of one project apart.
     *
     * One reading for both the slot which exists and the one an admission rule asked for and did not
     * find (#1705). A rule naming `staging [demo]` has to name it on the map exactly as the real
     * `staging [demo]` would be named, or the two cannot be compared by eye - which is the whole
     * point of drawing the unresolved one.
     */
    fun label(environmentName: String, qualifier: String): String =
        environmentName + (qualifier.takeIf { it.isNotBlank() }?.let { " [$it]" } ?: "")

}

/**
 * Payload of a [SlotDeliveryMapCheckpoints.SLOT] checkpoint.
 *
 * @property slotId What the renderer links to. The slot page is where the admission rules the map
 * only draws the shape of are actually readable.
 * @property unreachable No build of this branch can ever be deployed here, because an admission rule
 * excludes the branch outright. Such a checkpoint carries no build: "you cannot get there from here"
 * is the answer, and a build sitting beside it would argue with it.
 * @property otherBranch Branch of the deployed build when it is *not* the branch of the map, and
 * null when it is. A slot names the most recently deployed build whatever its branch - the deliberate
 * exception recorded in ADR 0009 - so the renderer has to be able to say so.
 */
data class SlotCheckpointData(
    @APIDescription("ID of the slot")
    val slotId: String,
    @APIDescription("Can no build of this branch ever be deployed here?")
    val unreachable: Boolean,
    @APIDescription("Branch of the deployed build, when it is not the branch of the map")
    val otherBranch: String?,
)

/**
 * Payload of a [SlotDeliveryMapCheckpoints.SLOT_WORKFLOW] checkpoint.
 *
 * The checkpoint carries no [net.nemerosa.ontrack.model.deliverymap.DeliveryMapArrival]: its build
 * would always be the one the slot beside it already names, so an arrival here would repeat that
 * build one node further along. What became of the run is said by [status] instead.
 *
 * Everything but [slotWorkflowId] and [trigger] is null on a workflow which has never run, which is
 * a state a slot workflow really has and a promotion workflow cannot: slot workflows are
 * configuration, and are drawn whether or not any pipeline ever reached them.
 *
 * @property slotWorkflowId The configured workflow this checkpoint is, stable across pipelines
 * @property trigger Which moment of a deployment fires it. `CANDIDATE` and `RUNNING` are hard gates
 * - a deployment cannot start, or cannot finish, until they pass - which is why those two are drawn
 * as *requires* into the slot while `DONE` is drawn as *emits* out of it.
 * @property workflowInstanceId The run to link to, null when it has never run
 * @property status Where the run got to, null when it has never run
 * @property startTime When the run started, null when it has never run or no node has started. The
 * renderer counts elapsed time from it while the run is unfinished, because [durationMs] is 0 until
 * the last node ends.
 * @property durationMs How long the run took, 0 while it is still running and null when it has
 * never run
 */
data class SlotWorkflowCheckpointData(
    @APIDescription("ID of the workflow configured on the slot")
    val slotWorkflowId: String,
    @APIDescription("Moment of a deployment which fires this workflow")
    val trigger: SlotPipelineStatus,
    @APIDescription("ID of the workflow instance, null when the workflow has never run")
    val workflowInstanceId: String?,
    @APIDescription("Status of the workflow instance, null when the workflow has never run")
    val status: WorkflowInstanceStatus?,
    @APIDescription("When the workflow started, null when it has never run")
    val startTime: LocalDateTime?,
    @APIDescription("How long the workflow took, 0 while running and null when it has never run")
    val durationMs: Long?,
)
