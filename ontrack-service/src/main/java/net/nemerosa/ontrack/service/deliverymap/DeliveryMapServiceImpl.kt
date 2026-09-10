package net.nemerosa.ontrack.service.deliverymap

import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Assembles the delivery map of a branch out of its contributors.
 *
 * This service owns four rules, and deliberately no more: everything about *what* a checkpoint is
 * belongs to the contributor which knows the configuration behind it. The fourth is the branch head
 * and the lag counted from it, which is here rather than in the contributors precisely because every
 * checkpoint has to answer it the same way - a lag measured differently by two contributors would be
 * read as a difference between the checkpoints rather than between the contributors.
 */
@Service
class DeliveryMapServiceImpl(
    private val contributors: List<DeliveryMapContributor>,
    private val securityService: SecurityService,
    private val structureService: StructureService,
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

        // One count per BUILD rather than per checkpoint: every promotion level of a branch routinely
        // names the same build, and a map of twenty checkpoints would otherwise run twenty queries to
        // arrive at the same three answers.
        val head = structureService.getLastBuildForBranch(branch)
        val lags = mutableMapOf<Int, Int>()

        return DeliveryMap(
            checkpoints = checkpoints.values.map { withLag(it, branch, head, lags) },
            edges = edges,
            head = head,
        )
    }

    /**
     * Fills in the lag of a checkpoint's arrival, and of its members'.
     *
     * The members of an aggregate are covered because they name builds of their own: a member which
     * showed a build without saying how old it is would be the one place on the map where the
     * question goes unanswered.
     */
    private fun withLag(
        checkpoint: DeliveryMapCheckpoint,
        branch: Branch,
        head: Build?,
        lags: MutableMap<Int, Int>,
    ): DeliveryMapCheckpoint =
        checkpoint.copy(
            arrival = checkpoint.arrival?.let { it.copy(lag = lag(it.build, branch, head, lags)) },
            members = checkpoint.members.map { withLag(it, branch, head, lags) },
        )

    /**
     * How far behind the branch's head the given [build] is, or null when the question does not apply.
     */
    private fun lag(build: Build, branch: Branch, head: Build?, lags: MutableMap<Int, Int>): Int? =
        when {
            // Nothing to be behind: a branch with no build at all
            head == null -> null
            // A slot names the most recently deployed build, whatever branch it belongs to (ADR 0009).
            // Counting that against THIS branch's head would answer a question nobody asked, and would
            // do it in a number - which reads as a fact rather than as a category error.
            build.branch.id() != branch.id() -> null
            // The common case, and the one worth not paying a query for
            build.id() == head.id() -> 0
            else -> lags.getOrPut(build.id()) { structureService.getNewerBuildCount(build) }
        }

    companion object {
        private val logger = LoggerFactory.getLogger(DeliveryMapServiceImpl::class.java)
    }
}
