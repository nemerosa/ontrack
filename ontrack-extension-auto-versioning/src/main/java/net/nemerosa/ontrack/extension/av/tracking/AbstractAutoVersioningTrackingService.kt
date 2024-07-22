package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.model.structure.*
import kotlin.jvm.optionals.getOrNull

abstract class AbstractAutoVersioningTrackingService(
    private val entityStore: EntityStore,
    private val structureService: StructureService,
) : AutoVersioningTrackingService {

    private abstract class AbstractAutoVersioningTracking: AutoVersioningTracking {

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
        val trail = entityStore.findByName<StoredAutoVersioningTrail>(run, STORE, STORE_KEY)
            ?.toModel()
            ?: AutoVersioningTrail.empty()
        // Updates the trail
        val updatedTrail = code(trail)
        // Saves the new trail
        entityStore.store(run, STORE, STORE_KEY, updatedTrail.toStore())
        // OK
        return updatedTrail
    }

    override fun getTrail(run: PromotionRun): AutoVersioningTrail? {
        return entityStore.findByName<StoredAutoVersioningTrail>(run, STORE, STORE_KEY)
            ?.toModel()
    }

    private fun AutoVersioningBranchTrail.toStore() = StoredBranchTrail(
        id = id,
        project = branch.project.name,
        branch = branch.name,
        configuration = configuration,
        rejectionReason = rejectionReason,
        orderId = orderId,
    )

    private fun AutoVersioningTrail.toStore() = StoredAutoVersioningTrail(
        branches = branches.map {
            it.toStore()
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

    private fun StoredAutoVersioningTrail.toModel() = AutoVersioningTrail(
        branches = branches.mapNotNull {
            it.toModel()
        }
    )

    companion object {
        private const val STORE = "AutoVersioningTracking"
        private const val STORE_KEY = "trail"
    }

    data class StoredAutoVersioningTrail(
        val branches: List<StoredBranchTrail>,
    )

    data class StoredBranchTrail(
        val id: String,
        val project: String,
        val branch: String,
        val configuration: AutoVersioningSourceConfig,
        val rejectionReason: String?,
        val orderId: String?,
    )

}