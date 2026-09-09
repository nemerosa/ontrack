package net.nemerosa.ontrack.service.deliverymap

import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Assembles the delivery map of a branch out of its contributors.
 *
 * This service owns three rules, and deliberately no more: everything about *what* a checkpoint is
 * belongs to the contributor which knows the configuration behind it.
 */
@Service
class DeliveryMapServiceImpl(
    private val contributors: List<DeliveryMapContributor>,
    private val securityService: SecurityService,
) : DeliveryMapService {

    override fun getDeliveryMap(branch: Branch): DeliveryMap {
        securityService.checkProjectFunction(branch, ProjectView::class.java)

        // A contributor reads configuration owned by an extension, and a broken one must cost its own
        // contribution rather than the whole map - the same trade `DecorationServiceImpl` makes
        val contributions = contributors.mapNotNull { contributor ->
            try {
                contributor.contribute(branch)
            } catch (ex: Exception) {
                logger.error(
                    "Delivery map contributor ${contributor::class.java.name} failed on branch " +
                            branch.entityDisplayName,
                    ex
                )
                null
            }
        }

        // Checkpoint ids are unique across kinds, so two contributors reaching the same checkpoint
        // contribute the same node and the first one to describe it wins. Insertion order is kept:
        // it is what the layout reads as the order of the map's inputs.
        val checkpoints = LinkedHashMap<String, DeliveryMapCheckpoint>()
        contributions.flatMap { it.checkpoints }.forEach { checkpoints.putIfAbsent(it.id, it) }

        // An edge with an end which is not on the map is dropped, together with whatever it would
        // have pointed at. That is how a checkpoint left out by its contributor - because the user
        // may not see it - takes its edges with it, and it is why a missing end never renders as an
        // unresolved checkpoint: the unresolved checkpoints of #1705 are contributed on purpose, by
        // a contributor which knows a rule pointed at nothing, and are real nodes of the map.
        val edges = contributions.flatMap { it.edges }
            .filter { it.source in checkpoints && it.target in checkpoints }
            .distinctBy { it.id }

        return DeliveryMap(
            checkpoints = checkpoints.values.toList(),
            edges = edges,
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DeliveryMapServiceImpl::class.java)
    }
}
