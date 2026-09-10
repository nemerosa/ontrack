package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import java.time.LocalDateTime

/**
 * The latest build to have arrived at a checkpoint, and what became of it there.
 *
 * A promotion level is arrived at by being promoted, a validation stamp by a run of *any* outcome,
 * a slot by a deployment. Arriving is therefore not the same as succeeding, which is why [status]
 * exists at all.
 *
 * @property build On a promotion level and a validation stamp this build is always of the branch the
 * map belongs to; on a slot it is the most recently deployed build, which may belong to another branch.
 * @property status What became of the build at this checkpoint, when the checkpoint has something to
 * say about it. Null on a promotion level, where arriving *is* the outcome.
 * @property lag How many builds of the map's branch are more recent than [build]: 0 when it is the
 * branch's own head, and the number of builds it is behind otherwise. A build named by a checkpoint
 * means little until you know how far the branch has moved on since.
 *
 * Null when the question cannot be answered rather than when the answer is zero: on a slot naming
 * another branch's build (ADR 0009), where counting against this branch's head would answer a
 * question nobody asked, and on a branch with no head at all.
 */
@APIDescription("Latest build to have arrived at a checkpoint, and what became of it there")
data class DeliveryMapArrival(
    @APIDescription("Latest build to have arrived at this checkpoint")
    val build: Build,
    @APIDescription("When the build arrived")
    val time: LocalDateTime,
    @APIDescription("What became of the build here, null when arriving is the whole outcome")
    val status: ValidationRunStatusID? = null,
    @APIDescription("Builds of the branch more recent than this one, null when it cannot be counted")
    val lag: Int? = null,
)
