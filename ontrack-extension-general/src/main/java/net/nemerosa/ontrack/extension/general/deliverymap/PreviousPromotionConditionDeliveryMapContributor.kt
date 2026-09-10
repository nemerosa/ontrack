package net.nemerosa.ontrack.extension.general.deliverymap

import net.nemerosa.ontrack.extension.general.PreviousPromotionConditionService
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Draws the *requires* edges the previous-promotion condition produces: a promotion level cannot be
 * reached before the one immediately below it in the branch's order.
 *
 * The condition is the third way promotion levels depend on each other, beside auto promotion which
 * grants and promotion dependencies which block. It differs from both in being resolved through a
 * cascade - promotion level, then branch, then project, then the global settings - so the answer for
 * a promotion level routinely comes from somewhere else entirely.
 *
 * The map draws the edge wherever the condition **resolves** to `true`, through that whole cascade,
 * and not only where the property is set on the promotion level itself. Drawing only the latter would
 * be a strict subset of what actually blocks, and a reader takes the absence of an edge as "nothing
 * stops me". The map's contract is what constrains reaching a checkpoint on this branch, not where
 * the constraint happens to be configured - which is why no edge says where it came from. The
 * question a surprised user asks is "why can't I promote", and the promotion attempt answers it
 * exactly: `PreviousPromotionRequiredException` names the deciding entity.
 *
 * It reuses [DeliveryMapEdgeKind.REQUIRES] rather than adding a kind. The constraint *is* a requires,
 * and a reader made to learn a fourth word to be told the same fact is worse off. A useful side
 * effect: where a `PromotionDependenciesProperty` already names the same pair, both contributors
 * build the same edge id and `DeliveryMapServiceImpl` collapses them - one real constraint, one line.
 *
 * Only the immediate predecessor is ever named, and only for a promotion level which has one, so a
 * branch with n promotion levels draws at most n-1 edges: one path down an order the layout already
 * renders in that direction, not a mesh over every pair.
 *
 * Contributes no checkpoint: both ends of every edge here are promotion levels, which the core puts
 * on the map already.
 */
@Component
class PreviousPromotionConditionDeliveryMapContributor(
    private val structureService: StructureService,
    private val previousPromotionConditionService: PreviousPromotionConditionService,
) : DeliveryMapContributor {

    override fun contribute(branch: Branch): DeliveryMapContribution {
        // The branch's own promotion level order, which is the order the condition is expressed
        // against: the check extension names `promotions[index - 1]` out of this very list
        val promotionLevels = structureService.getPromotionLevelListForBranch(branch.id)

        // Resolved for the whole branch in one go: every level under it shares a branch and a
        // project, and asking one level at a time would re-read both of those properties - a query
        // each - once per level, on every refresh of the map
        val resolutions = previousPromotionConditionService.resolvePreviousPromotionConditions(promotionLevels)

        val edges = promotionLevels.drop(1).mapIndexedNotNull { index, promotionLevel ->
            // `drop(1)` shifted the indices, so the predecessor of the element at `index` is the one
            // at `index` in the original list
            val previous = promotionLevels[index]
            // `getValue`, not a null-tolerant read: the service answers for every level it was
            // given, and quietly treating a missing answer as "not required" would produce exactly
            // the under-drawn map this contributor exists to avoid. A missing key is a bug, and
            // `DeliveryMapServiceImpl` isolates a throwing contributor.
            val resolution = resolutions.getValue(promotionLevel.id)
            if (resolution.required) {
                DeliveryMapEdge.of(
                    kind = DeliveryMapEdgeKind.REQUIRES,
                    source = DeliveryMapCheckpointTypes.promotionLevel(previous.id),
                    target = DeliveryMapCheckpointTypes.promotionLevel(promotionLevel.id),
                )
            } else {
                null
            }
        }

        return DeliveryMapContribution(edges = edges)
    }

}
