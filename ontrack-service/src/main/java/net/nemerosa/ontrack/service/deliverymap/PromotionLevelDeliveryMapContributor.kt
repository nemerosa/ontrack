package net.nemerosa.ontrack.service.deliverymap

import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpointFactory
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapContribution
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapContributor
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Puts every promotion level of the branch on the delivery map.
 *
 * *Every* one, connected or not: a promotion level is something a build has to pass through on its
 * way to an environment whether or not anything is configured to grant it yet, and a map which hid
 * the ones with no configuration would answer "what is left to configure?" with silence.
 *
 * Validation stamps are the opposite case and are contributed elsewhere, by whoever draws an edge to
 * them: an unconnected stamp teaches nothing on a map whose subject is dependencies, and a branch
 * with forty of them would have no readable layout.
 *
 * This contributor draws no edges. Both of the properties which make promotion levels depend on each
 * other belong to `ontrack-extension-general`, not to the core.
 */
@Component
class PromotionLevelDeliveryMapContributor(
    private val structureService: StructureService,
    private val checkpointFactory: DeliveryMapCheckpointFactory,
) : DeliveryMapContributor {

    override fun contribute(branch: Branch): DeliveryMapContribution =
        DeliveryMapContribution(
            checkpoints = structureService.getPromotionLevelListForBranch(branch.id)
                .map(checkpointFactory::promotionLevel),
        )

}
