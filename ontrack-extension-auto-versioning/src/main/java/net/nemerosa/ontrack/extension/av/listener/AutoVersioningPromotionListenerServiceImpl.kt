package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.Branch
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
        logger.debug("Looking for configured branches: ${promotionRun.build.entityDisplayName} @ ${promotionRun.promotionLevel.name}...")
        // Gets all the trigger configurations about this event, based on project & promotion only
        val branches = autoVersioningConfigurationService.getBranchesConfiguredFor(promotionRun.build.project.name, promotionRun.promotionLevel.name)
        logger.debug("Raw configured branches for ${promotionRun.build.entityDisplayName} @ ${promotionRun.promotionLevel.name}: ${branches.size}")
        // Filters the branches based on their configuration and the triggering event
        val configuredBranches = branches
            .filter { !it.isDisabled && !it.project.isDisabled }
            .mapNotNull {
                filterBranch(it, promotionRun)
            }
        // Logging
        if (logger.isDebugEnabled) {
            configuredBranches.forEach { configuredBranch ->
                configuredBranch.configurations.forEach { config ->
                    logger.debug("Configured branch: ${configuredBranch.branch.entityDisplayName} -> $config")
                }
            }
        }
        // OK
        return AutoVersioningConfiguredBranches(
            configuredBranches,
            promotionRun
        )
    }

    private fun filterBranch(
        branch: Branch,
        promotionRun: PromotionRun,
    ): AutoVersioningConfiguredBranch? {
        logger.debug("Filtering branch {} for event {}...", branch.entityDisplayName, promotionRun)
        // The project must be configured for a SCM
        if (scmDetector.getSCM(branch.project) != null) {
            // Gets the auto versioning configuration for the branch
            val config = autoVersioningConfigurationService.getAutoVersioning(branch)
            if (config != null) {
                logger.debug("Auto versioning config present. Filtering the configurations...")
                // If present, gets its configurations
                val autoVersioningConfigurations = config.configurations
                    // Filters the configurations based on the event
                    .filter { configuration -> promotionRun.match(branch, configuration) }
                    // Removes any empty setup
                    .takeIf { configurations -> configurations.isNotEmpty() }
                // Accepting the branch based on having at least one matching configuration
                return autoVersioningConfigurations?.let { list ->
                    AutoVersioningConfiguredBranch(branch, list)
                }
            }
            // No auto versioning property for this branch
            else {
                logger.debug("No auto versioning configuration at branch level")
                return null
            }
        }
        // No SCM configuration
        else {
            logger.debug("No supported SCM configured at project level")
            return null
        }
    }

    private fun PromotionRun.match(
        eligibleTargetBranch: Branch,
        config: AutoVersioningSourceConfig,
    ): Boolean {
        if (logger.isDebugEnabled) {
            logger.debug(
                """
                        | Promotion event matching:
                        |    event=$this
                        |    config=$config
                        |    """.trimMargin()
            )
        }
        val match = config.sourceProject == build.project.name &&
                config.sourcePromotion == promotionLevel.name &&
                sourceBranchMatch(eligibleTargetBranch, config)
        if (logger.isDebugEnabled) {
            logger.debug("Promotion event matching: $match")
        }
        return match
    }

    private fun PromotionRun.sourceBranchMatch(
        eligibleTargetBranch: Branch,
        config: AutoVersioningSourceConfig,
    ): Boolean {
        logger.debug("""Promotion event matching based on branch latest promotion""")
        val latestSourceBranch =
            autoVersioningConfigurationService.getLatestBranch(eligibleTargetBranch, build.project, config)
        logger.debug("Latest branch: ${latestSourceBranch?.name}")
        // We want the promoted build to be on the latest source branch
        return latestSourceBranch != null && latestSourceBranch.id() == build.branch.id()
    }

}