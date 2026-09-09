package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationStamp

/**
 * Builds the checkpoints of the entities the core owns, so that every contributor drawing a promotion
 * level or a validation stamp draws it the same way - the same id, the same payload, and above all the
 * same answer to "which build arrived here, and what became of it".
 *
 * A contributor lives in an extension and cannot reach into `ontrack-service`, which is why this is an
 * interface of the model rather than a helper class.
 */
interface DeliveryMapCheckpointFactory {

    /**
     * Builds the checkpoint of a [promotionLevel], arrived at by being promoted.
     */
    fun promotionLevel(promotionLevel: PromotionLevel): DeliveryMapCheckpoint

    /**
     * Builds the checkpoint of a [validationStamp], arrived at by a run of any outcome.
     */
    fun validationStamp(validationStamp: ValidationStamp): DeliveryMapCheckpoint

}
