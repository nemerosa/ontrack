package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.common.logTime
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningPromotionListenerServiceImpl(
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
    private val scmDetector: SCMDetector,
) : AutoVersioningPromotionListenerService {

    private val logger: Logger = LoggerFactory.getLogger(AutoVersioningPromotionListenerServiceImpl::class.java)

    override fun getConfiguredBranches(promotionRun: PromotionRun): AutoVersioningConfiguredBranches? {
        // Gets all the trigger configurations about this event, based on project & promotion only
        val branches = autoVersioningConfigurationService.getBranchesConfiguredFor(
            promotionRun.build.project.name,
            promotionRun.promotionLevel.name
        )
        // Filters the branches based on their configuration and the triggering event
        val cache = mutableMapOf<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>()
        val configuredBranches = branches
            .filter { !it.isDisabled && !it.project.isDisabled }
            .mapNotNull {
                filterBranch(it, promotionRun, cache)
            }
        logger.debug("Cache: {}", cache.keys)
        // OK
        return AutoVersioningConfiguredBranches(
            configuredBranches,
            promotionRun
        )
    }

    private fun filterBranch(
        branch: Branch,
        promotionRun: PromotionRun,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
    ): AutoVersioningConfiguredBranch? {
        // The project must be configured for a SCM
        if (scmDetector.getSCM(branch.project) != null) {
            // Gets the auto versioning configuration for the branch
            val config = autoVersioningConfigurationService.getAutoVersioning(branch)
            if (config != null) {
                // If present, gets its configurations
                val autoVersioningConfigurations = config.configurations
                    // Filters the configurations based on the event
                    .filter { configuration -> promotionRun.match(branch, configuration, cache) }
                    // Removes any empty setup
                    .takeIf { configurations -> configurations.isNotEmpty() }
                // Accepting the branch based on having at least one matching configuration
                return autoVersioningConfigurations?.let { list ->
                    AutoVersioningConfiguredBranch(branch, list)
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

    private fun PromotionRun.match(
        eligibleTargetBranch: Branch,
        config: AutoVersioningSourceConfig,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
    ): Boolean {
        val match = config.sourceProject == build.project.name &&
                config.sourcePromotion == promotionLevel.name &&
                logger.logTime("AV Source Branch match") {
                    sourceBranchMatch(eligibleTargetBranch, config, cache)
                }
        return match
    }

    private fun PromotionRun.sourceBranchMatch(
        eligibleTargetBranch: Branch,
        config: AutoVersioningSourceConfig,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
    ): Boolean {
        val cacheKey = LatestSourceBranchCacheKey(eligibleTargetBranch, build.project, config)
        val latestSourceBranch = cache.getOrPut(cacheKey) {
            LatestSourceBranchCacheValue(
                autoVersioningConfigurationService.getLatestBranch(eligibleTargetBranch, build.project, config)
            )
        }.branch
        // We want the promoted build to be on the latest source branch
        return latestSourceBranch != null && latestSourceBranch.id() == build.branch.id()
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