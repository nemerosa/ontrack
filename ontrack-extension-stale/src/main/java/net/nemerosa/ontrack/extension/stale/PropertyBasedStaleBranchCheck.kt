package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

/**
 * This [StaleBranchCheck] checks the staleness of a branch based on last activity (last build)
 * and a property set at project level.
 */
@Component
class PropertyBasedStaleBranchCheck(
        extensionFeature: StaleExtensionFeature,
        private val propertyService: PropertyService,
        private val structureService: StructureService
) : AbstractExtension(extensionFeature), StaleBranchCheck {

    private val logger: Logger = LoggerFactory.getLogger(PropertyBasedStaleBranchCheck::class.java)

    override fun isProjectEligible(project: Project): Boolean =
            propertyService.hasProperty(project, StalePropertyType::class.java)

    override fun isBranchEligible(branch: Branch): Boolean = true

    override fun getBranchStaleness(branch: Branch, lastBuild: Build?): StaleBranchStatus? {
        // Gets the project property
        val property: StaleProperty = propertyService.getProperty(branch.project, StalePropertyType::class.java).value
        // If no property, returns
                ?: return null
        // Disabling and deletion times
        val disablingDuration = property.disablingDuration
        val deletionDuration = property.deletingDuration
        val promotionsToKeep = property.promotionsToKeep
        if (disablingDuration <= 0) {
            logger.debug("[{}] No disabling time being set - exiting.", branch.entityDisplayName)
            return null
        } else {
            // Current time
            val now = Time.now()
            // Disabling time
            val disablingTime: LocalDateTime = now.minusDays(disablingDuration.toLong())
            // Deletion time
            val deletionTime: LocalDateTime? = if (deletionDuration != null && deletionDuration > 0) disablingTime.minusDays(deletionDuration.toLong()) else null
            // Logging
            logger.debug("[{}] Disabling time: {}", branch.entityDisplayName, disablingTime)
            logger.debug("[{}] Deletion time: {}", branch.entityDisplayName, deletionTime)
            // Indexation of promotion levels to protect
            val promotionsToProtect: Set<String> = if (promotionsToKeep != null) {
                HashSet(promotionsToKeep)
            } else {
                emptySet()
            }
            // Gets the last promotions for this branch
            val lastPromotions: List<PromotionView> = structureService.getBranchStatusView(branch).promotions
            val isProtected = lastPromotions.any { promotionView: PromotionView ->
                (promotionView.promotionRun != null
                        && promotionsToProtect.contains(promotionView.promotionLevel.name))
            }
            if (isProtected) {
                logger.debug("[{}] Branch is promoted and is not eligible for staleness", branch.entityDisplayName)
                return StaleBranchStatus.KEEP
            }
            // Last date
            val lastTime: LocalDateTime = lastBuild?.signature?.time ?: branch.signature.time
            // Deletion?
            return if (deletionTime != null && deletionTime > lastTime) {
                StaleBranchStatus.DELETE
            } else if (disablingTime > lastTime && !branch.isDisabled) {
                StaleBranchStatus.DISABLE
            } else {
                null
            }
        }
    }
}