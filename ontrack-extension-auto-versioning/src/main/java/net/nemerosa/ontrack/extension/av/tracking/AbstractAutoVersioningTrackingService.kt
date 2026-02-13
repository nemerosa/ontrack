package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.listener.AutoVersioningPromotionListenerService
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import kotlin.jvm.optionals.getOrNull

abstract class AbstractAutoVersioningTrackingService(
    private val avTrailRepository: AvTrailRepository,
    private val structureService: StructureService,
    private val autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService,
) : AutoVersioningTrackingService {

    private abstract class AbstractAutoVersioningTracking : AutoVersioningTracking {

        override fun init(configuredBranches: List<AutoVersioningConfiguredBranch>): List<AutoVersioningBranchTrail> =
            withTrail {
                it.init(configuredBranches)
            }.branches

    }

    override fun start(run: PromotionRun): AutoVersioningTracking {
        // Returning a callback
        return object : AbstractAutoVersioningTracking() {

            override fun withTrail(code: (trail: AutoVersioningTrail) -> AutoVersioningTrail): AutoVersioningTrail =
                withTrail(run, code)

            override val trail: AutoVersioningTrail?
                get() = getTrail(run)
        }
    }

    override fun startInMemoryTrail(): AutoVersioningTracking {
        var trail: AutoVersioningTrail? = null
        return object : AbstractAutoVersioningTracking() {

            override fun withTrail(code: (trail: AutoVersioningTrail) -> AutoVersioningTrail): AutoVersioningTrail {
                val updatedTrail = code(trail ?: AutoVersioningTrail.empty())
                trail = updatedTrail
                return updatedTrail
            }

            override val trail: AutoVersioningTrail?
                get() = trail
        }
    }

    override fun withTrail(
        run: PromotionRun,
        code: (trail: AutoVersioningTrail) -> AutoVersioningTrail
    ): AutoVersioningTrail {
        // Gets the stored version
        val trail = avTrailRepository.findByPromotionRun(run, filter = AutoVersioningTrailFilter.all)
            ?.toModel()
            ?: AutoVersioningTrail.empty()
        // Updates the trail
        val updatedTrail = code(trail)
        // Saves the new trail
        avTrailRepository.saveForPromotionRun(run, updatedTrail.toStore())
        // OK
        return updatedTrail
    }

    override fun getTrail(run: PromotionRun): AutoVersioningTrail? =
        avTrailRepository.findByPromotionRun(run, AutoVersioningTrailFilter.all)?.toModel()

    override fun getPaginatedTrail(
        run: PromotionRun,
        filter: AutoVersioningTrailFilter,
        offset: Int,
        size: Int
    ): PaginatedList<AutoVersioningBranchTrail> {
        val items = avTrailRepository.findBranchesByPromotionRun(run, filter, offset, size)
        val total = avTrailRepository.countBranchesByPromotionRun(run, filter)
        return PaginatedList.create(
            items = items,
            offset = offset,
            total = total,
            pageSize = size,
        ).mapNotNull { it.toModel() }
    }

    override fun getPromotionPaginatedTrail(
        promotionLevel: PromotionLevel,
        filter: AutoVersioningTrailFilter,
        offset: Int,
        size: Int
    ): PaginatedList<AutoVersioningBranchTrail> {
        val tracking = startInMemoryTrail()
        autoVersioningPromotionListenerService.getConfiguredBranches(promotionLevel, tracking)
        val trail = tracking.trail ?: AutoVersioningTrail(emptyList())
        val branchTrails = filter.filter(trail.branches)
        return PaginatedList.create(
            items = branchTrails,
            offset = offset,
            pageSize = size,
            total = branchTrails.size,
        )
    }

    private fun AutoVersioningTrail.toStore() = StoredAutoVersioningTrail(
        branches = branches.map {
            it.toStore()
        }
    )

    private fun AutoVersioningBranchTrail.toStore() = StoredBranchTrail(
        id = id,
        project = branch.project.name,
        branch = branch.name,
        configuration = configuration,
        rejectionReason = rejectionReason,
        orderId = orderId,
    )

    private fun StoredAutoVersioningTrail.toModel() = AutoVersioningTrail(
        branches = branches.mapNotNull {
            it.toModel()
        }
    )

    private fun StoredBranchTrail.toModel(): AutoVersioningBranchTrail? =
        structureService.findBranchByName(project, branch).getOrNull()?.let { branch ->
            AutoVersioningBranchTrail(
                id = id,
                branch = branch,
                configuration = configuration,
                rejectionReason = rejectionReason,
                orderId = orderId,
            )
        }

}