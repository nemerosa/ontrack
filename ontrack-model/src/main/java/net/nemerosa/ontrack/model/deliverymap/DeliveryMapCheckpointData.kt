package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription

/**
 * Payload of a [DeliveryMapCheckpointTypes.PROMOTION_LEVEL] checkpoint.
 *
 * It carries what the checkpoint's renderer needs on top of the name and the description every
 * checkpoint has: the id to link to the promotion level, and whether it has an image to draw.
 *
 * @property promotionRunId The run the checkpoint's own arrival is, and therefore the run whose
 * workflows are drawn beside this checkpoint (#1711). [DeliveryMapArrival] names the build and not
 * the run, and a build can be promoted to one level several times, so without this the map states
 * an arrival and a set of workflows with nothing saying they belong to the same promotion. Null on
 * a promotion level nothing has ever been promoted to, which is also the case with no workflows.
 */
data class PromotionLevelCheckpointData(
    @APIDescription("ID of the promotion level")
    val promotionLevelId: Int,
    @APIDescription("Does the promotion level have an image?")
    val image: Boolean,
    @APIDescription("ID of the promotion run the checkpoint's arrival is, null when never promoted")
    val promotionRunId: Int? = null,
)

/**
 * Payload of a [DeliveryMapCheckpointTypes.VALIDATION_STAMP] checkpoint.
 */
data class ValidationStampCheckpointData(
    @APIDescription("ID of the validation stamp")
    val validationStampId: Int,
    @APIDescription("Does the validation stamp have an image?")
    val image: Boolean,
)

/**
 * Payload of a [DeliveryMapCheckpointTypes.VALIDATION_STAMP_PATTERN] aggregate checkpoint.
 *
 * The patterns are what the checkpoint is labelled with, because they are what the configuration
 * actually says: everything matching this, not these forty named things.
 */
data class ValidationStampPatternCheckpointData(
    @APIDescription("ID of the promotion level whose auto promotion carries the patterns")
    val promotionLevelId: Int,
    @APIDescription("Regular expression including validation stamps by name")
    val include: String,
    @APIDescription("Regular expression excluding validation stamps by name, blank when there is none")
    val exclude: String,
)

/**
 * Payload of a [DeliveryMapCheckpointTypes.UNRESOLVED] checkpoint.
 *
 * The name the configuration asked for is the checkpoint's own name; what the payload adds is *what
 * kind of thing* was looked for, so that the renderer can say which one it is - "no promotion level
 * of this name on this branch" reads very differently from "no slot of this project in that
 * environment".
 *
 * @property reference Checkpoint kind the configuration named, as a [DeliveryMapCheckpointTypes]
 * constant or an extension's own. A renderer which does not recognise it still has the name and can
 * fall back to saying that it matches nothing.
 */
data class UnresolvedCheckpointData(
    @APIDescription("Kind of checkpoint the configuration asked for and did not find")
    val reference: String,
)
