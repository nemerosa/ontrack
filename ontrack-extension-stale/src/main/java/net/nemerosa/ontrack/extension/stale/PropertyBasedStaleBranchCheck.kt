package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * This [StaleBranchCheck] checks the staleness of a branch based on last activity (last build)
 * and a property set at project level.
 */
@Component
class PropertyBasedStaleBranchCheck(
    extensionFeature: StaleExtensionFeature,
    private val propertyService: PropertyService,
    private val promotionRunService: PromotionRunService,
) : AbstractExtension(extensionFeature), StaleBranchCheck {

    private val logger: Logger = LoggerFactory.getLogger(PropertyBasedStaleBranchCheck::class.java)

    override fun isProjectEligible(project: Project): Boolean =
        propertyService.hasProperty(project, StalePropertyType::class.java)

    override fun isBranchEligible(branch: Branch): Boolean = true

    override fun getBranchStaleness(
        context: StaleBranchCheckContext,
        branch: Branch,
        lastBuild: Build?
    ): StaleBranchStatus? {
        // Gets the project property
        val property: StaleProperty = propertyService.getProperty(branch.project, StalePropertyType::class.java).value
        // If no property, returns
            ?: return null
        // Disabling and deletion times
        val disablingDuration = property.disablingDuration
        val deletionDuration = property.deletingDuration
        val promotionsToKeep = property.promotionsToKeep
        val includesRegex = property.includes?.takeIf { it.isNotBlank() }?.toRegex()
        val excludesRegex = property.excludes?.takeIf { it.isNotBlank() }?.toRegex()
        if (disablingDuration <= 0) {
            logger.debug("[{}] No disabling time being set - exiting.", branch.entityDisplayName)
            return null
        } else {
            // Current time
            val now = Time.now()
            // Disabling time
            val disablingTime: LocalDateTime = now.minusDays(disablingDuration.toLong())
            // Deletion time
            val deletionTime: LocalDateTime? =
                if (deletionDuration != null && deletionDuration > 0) disablingTime.minusDays(deletionDuration.toLong()) else null
            // Logging
            logger.debug("[{}] Disabling time: {}", branch.entityDisplayName, disablingTime)
            logger.debug("[{}] Deletion time: {}", branch.entityDisplayName, deletionTime)
            // Indexation of promotion levels to protect
            val promotionsToProtect = promotionsToKeep?.toSet() ?: emptySet()
            // Gets the last promotions for this branch
            if (promotionsToProtect.isNotEmpty()) {
                val isProtected = promotionsToProtect.any { promotionName ->
                    promotionRunService.getLastPromotionRunForBranch(branch, promotionName) != null
                }
                if (isProtected) {
                    logger.debug("[{}] Branch is promoted and is not eligible for staleness", branch.entityDisplayName)
                    return StaleBranchStatus.KEEP
                }
            }
            // Includes / excludes rules
            if (includesRegex != null) {
                if (includesRegex.matches(branch.name)) {
                    if (excludesRegex == null || !excludesRegex.matches(branch.name)) {
                        logger.debug("[{}] Branch is protected by includes/excludes rule", branch.entityDisplayName)
                        return StaleBranchStatus.KEEP
                    }
                }
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