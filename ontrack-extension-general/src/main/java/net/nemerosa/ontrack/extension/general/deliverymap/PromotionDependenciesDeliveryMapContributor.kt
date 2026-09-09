package net.nemerosa.ontrack.extension.general.deliverymap

import net.nemerosa.ontrack.extension.general.PromotionDependenciesPropertyType
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Draws the *requires* edges of the delivery map: a promotion level cannot be reached until another
 * has been.
 *
 * Kept apart from the *unlocks* edges of auto promotion because the two are expressed in ways with
 * opposite effects. A promotion dependency constrains; it does not act. A map drawing them the same
 * way would say that a configuration grants a promotion when it only permits it.
 *
 * Contributes no checkpoint: both ends of every edge here are promotion levels, which the core puts
 * on the map already.
 *
 * `PreviousPromotionConditionProperty` is deliberately not a source here. It is not per-promotion
 * configuration, it cascades from promotion level down to a global setting, and if that setting is on
 * it would draw a requires edge between every consecutive pair of promotion levels on every branch -
 * a chain over the whole map arriving as a side effect rather than as a decision. It is filed as #1710.
 */
@Component
class PromotionDependenciesDeliveryMapContributor(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
) : DeliveryMapContributor {

    override fun contribute(branch: Branch): DeliveryMapContribution {
        val promotionLevels = structureService.getPromotionLevelListForBranch(branch.id)
        val byName = promotionLevels.associateBy { it.name }

        val edges = promotionLevels.flatMap { promotionLevel ->
            val dependencies = propertyService
                .getPropertyValue(promotionLevel, PromotionDependenciesPropertyType::class.java)
                ?.dependencies
                ?: emptyList()
            val target = DeliveryMapCheckpointTypes.promotionLevel(promotionLevel.id)
            dependencies.mapNotNull { name ->
                // The property names its dependencies rather than referencing them, so a rename or a
                // deletion leaves a name matching no promotion level of the branch. Nothing is drawn
                // for it here; surfacing configuration which points at nothing is #1705's subject.
                byName[name]?.let { dependency ->
                    DeliveryMapEdge.of(
                        kind = DeliveryMapEdgeKind.REQUIRES,
                        source = DeliveryMapCheckpointTypes.promotionLevel(dependency.id),
                        target = target,
                    )
                }
            }
        }

        return DeliveryMapContribution(edges = edges)
    }

}
