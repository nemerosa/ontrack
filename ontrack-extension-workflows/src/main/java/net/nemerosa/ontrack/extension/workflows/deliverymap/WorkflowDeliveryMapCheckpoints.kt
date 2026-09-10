package net.nemerosa.ontrack.extension.workflows.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStatus
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpointTypes
import java.time.LocalDateTime

/**
 * The checkpoint kind the workflows extension contributes to a delivery map.
 *
 * Like the environments extension's `slot`, this is a kind the core has never heard of: the core
 * names the kinds it contributes itself, and an extension names its own. The id is still built
 * through the core's [DeliveryMapCheckpointTypes.checkpointId], because checkpoint ids share one
 * namespace across every kind and that namespacing is the core's rule.
 */
object WorkflowDeliveryMapCheckpoints {

    /**
     * A workflow fired by the promotion of a build, drawn as a consequence of the promotion level.
     */
    const val WORKFLOW = "workflow"

    /**
     * Identified by the promotion level and by the workflow's NAME, never by the instance.
     *
     * [net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance.id] is a UUID minted per
     * run, so embedding one would change the map's topology on every promotion and force a full
     * relayout, moving every node the reader had placed - undoing #1707. The promotion side has no
     * configured entity to key on instead: a workflow reaches it through a notification
     * subscription, and the subscription is not reachable from the instance. The name therefore has
     * to serve as identity, and two subscriptions on one promotion level whose workflows share a
     * name collapse into one checkpoint. That is accepted, and recorded in the docs of #1711.
     *
     * A workflow name is free text, unlike an entity name, so it may hold the `:` this id uses as a
     * separator. That costs nothing here: the promotion level id is read from the left and the rest
     * is the name, so no two pairs can produce one id.
     */
    fun workflow(promotionLevelId: Int, workflowName: String): String =
        DeliveryMapCheckpointTypes.checkpointId(WORKFLOW, "$promotionLevelId:$workflowName")

}

/**
 * Payload of a [WorkflowDeliveryMapCheckpoints.WORKFLOW] checkpoint.
 *
 * The checkpoint carries no [net.nemerosa.ontrack.model.deliverymap.DeliveryMapArrival]: its build
 * would always be the one its promotion level already names, so an arrival line here would repeat
 * that build one node to the right and lag behind it. What became of the workflow is said by
 * [status] instead, which is a `WorkflowInstanceStatus` and deliberately not dressed up as the
 * `ValidationRunStatusID` an arrival carries.
 *
 * @property workflowInstanceId The run to link to, which is where the workflow's nodes are readable
 * @property status Where the run got to
 * @property startTime When the run started, null on a run whose nodes have not started yet. The
 * renderer counts the elapsed time from it while the run is unfinished, because [durationMs] is 0
 * until the last node ends - see #1711.
 * @property durationMs How long the run took, 0 while it is still going. Its meaning is the
 * engine's, unchanged: nothing is recomputed here.
 */
data class WorkflowCheckpointData(
    @APIDescription("ID of the workflow instance")
    val workflowInstanceId: String,
    @APIDescription("Status of the workflow instance")
    val status: WorkflowInstanceStatus,
    @APIDescription("When the workflow started, null when no node has started yet")
    val startTime: LocalDateTime?,
    @APIDescription("How long the workflow took, 0 while it is still running")
    val durationMs: Long,
)
