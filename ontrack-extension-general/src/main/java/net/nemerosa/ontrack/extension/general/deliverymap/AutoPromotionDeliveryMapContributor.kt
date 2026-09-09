package net.nemerosa.ontrack.extension.general.deliverymap

import net.nemerosa.ontrack.extension.general.AutoPromotionProperty
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Draws the *unlocks* edges of the delivery map: reaching one checkpoint grants another by itself,
 * as auto promotion does.
 *
 * This is also where the map's validation stamps come from. Only the stamps taking part in an edge
 * are drawn - an unconnected stamp teaches nothing on a map whose subject is dependencies - so the
 * contributor which draws the edges is the one which contributes the stamps.
 *
 * It is a plain Spring component and not an `AbstractExtension`: the delivery map seam is a
 * `List<DeliveryMapContributor>` injection rather than an extension point resolved through the
 * `ExtensionManager`.
 */
@Component
class AutoPromotionDeliveryMapContributor(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val checkpointFactory: DeliveryMapCheckpointFactory,
) : DeliveryMapContributor {

    override fun contribute(branch: Branch): DeliveryMapContribution {
        val promotionLevels = structureService.getPromotionLevelListForBranch(branch.id)
        val validationStamps = structureService.getValidationStampListForBranch(branch.id)

        val checkpoints = mutableListOf<DeliveryMapCheckpoint>()
        val edges = mutableListOf<DeliveryMapEdge>()

        promotionLevels.forEach { promotionLevel ->
            val property = propertyService.getPropertyValue(promotionLevel, AutoPromotionPropertyType::class.java)
                ?: return@forEach
            val target = DeliveryMapCheckpointTypes.promotionLevel(promotionLevel.id)

            // Promotion levels granting this one. Filtering the branch's own list is what keeps a
            // promotion level named by the property but no longer on the branch off the map.
            promotionLevels.filter { it in property }.forEach { required ->
                edges += DeliveryMapEdge.of(
                    kind = DeliveryMapEdgeKind.UNLOCKS,
                    source = DeliveryMapCheckpointTypes.promotionLevel(required.id),
                    target = target,
                )
            }

            // The stamp selection is the property's own `contains`, never a copy of it.
            // `AutoPromotionPrerequisites` exists precisely because two hand-written copies of this
            // rule drift; the map is a third consumer and must not become the copy which does.
            val selected = validationStamps.filter { it in property }
            // Naming a stamp is the more specific statement of the two, so a stamp matched both ways
            // stays its own checkpoint
            val (named, byPattern) = selected.partition { property.containsDirectValidationStamp(it) }

            named.forEach { validationStamp ->
                checkpoints += checkpointFactory.validationStamp(validationStamp)
                edges += DeliveryMapEdge.of(
                    kind = DeliveryMapEdgeKind.UNLOCKS,
                    source = DeliveryMapCheckpointTypes.validationStamp(validationStamp.id),
                    target = target,
                )
            }

            // "Only the stamps taking part in an edge" assumes most stamps take part in none, and a
            // pattern inverts that assumption: an include of ".*" makes every stamp connected, so a
            // branch with forty of them would draw forty edges converging on one promotion. They
            // collapse into a single checkpoint labelled with the pattern, which is also the truer
            // reading of the configuration - everything matching this, not these forty things.
            if (byPattern.isNotEmpty()) {
                val aggregate = aggregate(promotionLevel, property, byPattern)
                checkpoints += aggregate
                edges += DeliveryMapEdge.of(
                    kind = DeliveryMapEdgeKind.UNLOCKS,
                    source = aggregate.id,
                    target = target,
                )
            }
            // A pattern matching no stamp at all draws nothing. Surfacing configuration which points
            // at nothing is #1705's subject, and half-drawing it here would be undone there.
        }

        return DeliveryMapContribution(checkpoints = checkpoints, edges = edges)
    }

    private fun aggregate(
        promotionLevel: PromotionLevel,
        property: AutoPromotionProperty,
        members: List<ValidationStamp>,
    ) = DeliveryMapCheckpoint(
        // At most one aggregate per promotion level, which is what makes this id deterministic
        id = DeliveryMapCheckpointTypes.validationStampPattern(promotionLevel.id),
        type = DeliveryMapCheckpointTypes.VALIDATION_STAMP_PATTERN,
        name = property.include,
        description = if (property.exclude.isBlank()) {
            "Every validation stamp whose name matches ${property.include}"
        } else {
            "Every validation stamp whose name matches ${property.include} and does not match ${property.exclude}"
        },
        data = ValidationStampPatternCheckpointData(
            promotionLevelId = promotionLevel.id(),
            include = property.include,
            exclude = property.exclude,
        ).asJson(),
        // No arrival of its own: its members carry theirs, and what "arriving at forty stamps at
        // once" would mean is not something the configuration says
        arrival = null,
        members = members.map(checkpointFactory::validationStamp),
    )

}
