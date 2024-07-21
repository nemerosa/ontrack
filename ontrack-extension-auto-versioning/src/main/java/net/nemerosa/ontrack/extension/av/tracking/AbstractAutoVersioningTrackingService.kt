package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.model.structure.*
import kotlin.jvm.optionals.getOrNull

abstract class AbstractAutoVersioningTrackingService(
    private val entityStore: EntityStore,
    private val structureService: StructureService,
) : AutoVersioningTrackingService {

    override fun start(run: PromotionRun): AutoVersioningTracking {
        // Returning a callback
        return object : AutoVersioningTracking {
            override fun withTrail(code: (trail: AutoVersioningTrail) -> AutoVersioningTrail) {
                withTrail(run, code)
            }

            override val trail: AutoVersioningTrail?
                get() = getTrail(run)
        }
    }

    override fun startInMemoryTrail(): AutoVersioningTracking {
        var trail: AutoVersioningTrail? = null
        return object : AutoVersioningTracking {
            override fun withTrail(code: (trail: AutoVersioningTrail) -> AutoVersioningTrail) {
                trail = code(trail ?: AutoVersioningTrail.init())
            }

            override val trail: AutoVersioningTrail?
                get() = trail
        }
    }

    override fun withTrail(
        run: PromotionRun,
        code: (trail: AutoVersioningTrail) -> AutoVersioningTrail
    ) {
        // Gets the stored version
        val trail = entityStore.findByName<AutoVersioningTrail>(run, STORE, STORE_KEY)
            ?: AutoVersioningTrail.init()
        // Updates the trail
        val updatedTrail = code(trail)
        // Saves the new trail
        entityStore.store(run, STORE, STORE_KEY, updatedTrail.toStore())
    }

    override fun getTrail(run: PromotionRun): AutoVersioningTrail? {
        return entityStore.findByName<StoredAutoVersioningTrail>(run, STORE, STORE_KEY)
            ?.toModel()
    }

    private fun AutoVersioningTrail.toStore() = StoredAutoVersioningTrail(
        potentialTargetBranches = potentialTargetBranches.map {
            StoredBranch(
                project = it.project.name,
                branch = it.name,
            )
        },
        rejectedTargetBranches = rejectedTargetBranches.map {
            StoredRejectedBranch(
                branch = StoredBranch(
                    project = it.branch.project.name,
                    branch = it.branch.name,
                ),
                reason = it.reason,
            )
        }
    )

    private fun StoredAutoVersioningTrail.toModel() = AutoVersioningTrail(
        potentialTargetBranches = potentialTargetBranches.mapNotNull {
            it.toModel()
        },
        rejectedTargetBranches = rejectedTargetBranches.mapNotNull {
            it.toModel()
        },
    )

    private fun StoredBranch.toModel(): Branch? =
        structureService.findBranchByName(project, branch).getOrNull()

    private fun StoredRejectedBranch.toModel(): RejectedBranch? =
        branch.toModel()?.let {
            RejectedBranch(
                branch = it,
                reason = reason,
            )
        }

    companion object {
        private const val STORE = "AutoVersioningTracking"
        private const val STORE_KEY = "trail"
    }

    data class StoredAutoVersioningTrail(
        val potentialTargetBranches: List<StoredBranch>,
        val rejectedTargetBranches: List<StoredRejectedBranch>,
    )

    data class StoredBranch(
        val project: String,
        val branch: String,
    )

    data class StoredRejectedBranch(
        val branch: StoredBranch,
        val reason: String,
    )
}