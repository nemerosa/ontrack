package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.common.logTime
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.av.project.AutoVersioningProjectPropertyType
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class AutoVersioningPromotionListenerServiceImpl(
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
    private val scmDetector: SCMDetector,
    private val propertyService: PropertyService,
    private val structureService: StructureService,
) : AutoVersioningPromotionListenerService {

    private val logger: Logger = LoggerFactory.getLogger(AutoVersioningPromotionListenerServiceImpl::class.java)

    override fun getConfiguredBranches(promotionLevel: PromotionLevel): List<AutoVersioningConfiguredBranch> {
        // Gets all the trigger configurations about this event, based on project & promotion only
        val branches = autoVersioningConfigurationService.getBranchesConfiguredFor(
            promotionLevel.project.name,
            promotionLevel.name
        )
        // Filters the branches based on their configuration and the triggering event
        val cache = mutableMapOf<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>()
        val configuredBranches = branches
            .filter { !it.isDisabled && !it.project.isDisabled }
            .mapNotNull {
                filterBranch(it, promotionLevel, cache)
            }
        logger.debug("Cache: {}", cache.keys)
        // OK
        return configuredBranches
    }

    private fun filterBranch(
        branch: Branch,
        promotionLevel: PromotionLevel,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
    ): AutoVersioningConfiguredBranch? {
        // The project must be configured for a SCM
        if (scmDetector.getSCM(branch.project) != null) {
            // Gets the auto versioning configuration for the branch
            val config = autoVersioningConfigurationService.getAutoVersioning(branch)
            if (config != null) {
                // Target branch configured for auto-versioning, we still need to check the rules at the project level
                if (acceptBranchWithProjectAVRules(branch)) {
                    // If present, gets its configurations
                    val autoVersioningConfigurations = config.configurations
                        // Filters the configurations based on the event
                        .filter { configuration -> match(promotionLevel, branch, configuration, cache) }
                        // Removes any empty setup
                        .takeIf { configurations -> configurations.isNotEmpty() }
                    // Accepting the branch based on having at least one matching configuration
                    return autoVersioningConfigurations?.let { list ->
                        AutoVersioningConfiguredBranch(branch, list)
                    }
                } else {
                    // Not accepted by the project
                    return null
                }
            }
            // No auto versioning property for this branch
            else {
                // No auto versioning configuration at branch level
                return null
            }
        }
        // No SCM configuration
        else {
            // No supported SCM configured at project level
            return null
        }
    }

    override fun acceptBranchWithProjectAVRules(branch: Branch): Boolean {
        // Gets the auto-versioning property at project level
        val property = propertyService.getPropertyValue(branch.project, AutoVersioningProjectPropertyType::class.java)
            ?: return true // No rules ==> accepting by default
        // Branch inclusions
        val includes = property.branchIncludes.isNullOrEmpty() || property.branchIncludes.any { pattern ->
            branch.name.matches(pattern.toRegex())
        }
        if (!includes) return false // Rejected by the inclusion rule
        // Branch exclusions
        val excludes = !property.branchExcludes.isNullOrEmpty() && property.branchExcludes.any { pattern ->
            branch.name.matches(pattern.toRegex())
        }
        if (excludes) return false // Rejected by the exclusion rule
        // Last activity date rule
        if (property.lastActivityDate != null) {
            // Gets the last activity of the branch
            val lastBranchActivityDate = structureService.getLastBuild(branch.id).getOrNull()?.signature?.time
            if (lastBranchActivityDate != null) {
                // To be valid for AV, the branch activity date must be posterior to the threshold date
                return lastBranchActivityDate > property.lastActivityDate
            }
        }
        // OK, no rule matching
        return true
    }

    private fun match(
        sourcePromotion: PromotionLevel,
        eligibleTargetBranch: Branch,
        config: AutoVersioningSourceConfig,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
    ): Boolean {
        val match = config.sourceProject == sourcePromotion.project.name &&
                config.sourcePromotion == sourcePromotion.name &&
                logger.logTime("AV Source Branch match") {
                    sourceBranchMatch(sourcePromotion.branch, eligibleTargetBranch, config, cache)
                }
        return match
    }

    private fun sourceBranchMatch(
        sourceBranch: Branch,
        eligibleTargetBranch: Branch,
        config: AutoVersioningSourceConfig,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
    ): Boolean {
        val cacheKey = LatestSourceBranchCacheKey(eligibleTargetBranch, sourceBranch.project, config)
        val latestSourceBranch = cache.getOrPut(cacheKey) {
            LatestSourceBranchCacheValue(
                autoVersioningConfigurationService.getLatestBranch(eligibleTargetBranch, sourceBranch.project, config)
            )
        }.branch
        // We want the promoted build to be on the latest source branch
        return latestSourceBranch != null && latestSourceBranch.id() == sourceBranch.id()
    }

    private data class LatestSourceBranchCacheKey(
        private val targetBranchId: Int,
        private val sourceProjectId: Int,
        private val configSourceBranch: String,
        private val configSourcePromotion: String,
    ) {
        constructor(
            eligibleTargetBranch: Branch,
            sourceProject: Project,
            config: AutoVersioningSourceConfig,
        ) : this(
            targetBranchId = eligibleTargetBranch.id(),
            sourceProjectId = sourceProject.id(),
            configSourceBranch = config.sourceBranch,
            configSourcePromotion = config.sourcePromotion,
        )
    }

    private data class LatestSourceBranchCacheValue(
        val branch: Branch?,
    )

}