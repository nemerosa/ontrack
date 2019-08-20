package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.PromotionRunCheckExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Checks if a promotion can be granted according
 * to the promotion dependencies defined by the
 * [PromotionDependenciesPropertyType] property.
 */
@Component
class PromotionRunDependenciesCheckExtension(
        extensionFeature: GeneralExtensionFeature,
        private val propertyService: PropertyService,
        private val structureService: StructureService
) : AbstractExtension(extensionFeature), PromotionRunCheckExtension {

    override fun checkPromotionRunCreation(promotionRun: PromotionRun) {
        // Associated promotion
        val promotion = promotionRun.promotionLevel
        // Gets its dependencies
        val dependencies = propertyService
                .getProperty(promotion, PromotionDependenciesPropertyType::class.java)
                .value
                ?.dependencies
                ?: emptyList()
        // If there are any dependency, check that they are all set
        if (dependencies.isNotEmpty()) {
            // Build to promote
            val build = promotionRun.build
            // For each dependency
            for (dependency in dependencies) {
                // Gets the associated promotion
                val dependencyPromotion: PromotionLevel? = structureService.findPromotionLevelByName(
                        build.project.name,
                        build.branch.name,
                        dependency
                ).orElse(null)
                // Is the build promoted?
                if (dependencyPromotion != null) {
                    val dependencyPromotionRuns = structureService.getPromotionRunsForBuildAndPromotionLevel(
                            build,
                            dependencyPromotion
                    )
                    // If not promoted, we fail the check
                    if (dependencyPromotionRuns.isEmpty()) {
                        throw PromotionDependenciesException(
                                promotionRun,
                                dependencies,
                                dependency
                        )
                    }
                }
            }
        }
    }

    /**
     * After [PreviousPromotionConditionCheckExtension] has been applied.
     */
    override val order: Int = 1
}