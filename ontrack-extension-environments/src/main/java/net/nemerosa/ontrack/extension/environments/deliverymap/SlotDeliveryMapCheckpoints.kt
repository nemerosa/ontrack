package net.nemerosa.ontrack.extension.environments.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpointTypes

/**
 * The checkpoint kind the environments extension contributes to a delivery map.
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
