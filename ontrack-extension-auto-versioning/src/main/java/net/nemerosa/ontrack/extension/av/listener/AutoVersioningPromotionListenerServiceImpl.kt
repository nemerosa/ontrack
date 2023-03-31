package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.av.model.PromotionEvent
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningPromotionListenerServiceImpl(
    private val buildDisplayNameService: BuildDisplayNameService,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
    private val scmDetector: SCMDetector,
) : AutoVersioningPromotionListenerService {

    private val logger: Logger = LoggerFactory.getLogger(AutoVersioningPromotionListenerServiceImpl::class.java)

    override fun getConfiguredBranches(build: Build, promotion: PromotionLevel): AutoVersioningConfiguredBranches? {
        logger.debug("Looking for configured branches: ${build.entityDisplayName} @ ${promotion.name}...")
        // Gets the build name or version
        // We need to check if the build has any eligible version, based on its settings.
        // For example, a build having a "build display name" property stating that the label
        // must be used, if not having a label, won't have any eligible version.
        val version = buildDisplayNameService.getEligibleBuildDisplayName(build)
        // If no version can be gotten from the build, we don't return any configuration
        if (version == null) {
            // Logging the event
            logger.info("Build ${build.id} (${build.entityDisplayName}) was promoted, " +
                    "but is not eligible to auto versioning because no version was returned. " +
                    "This can typically be due to the fact that its project requires a label " +
                    "and the build has none.")
            // Not returning any config
            return null
        }
        // Creation of the event
        val promotionEvent = PromotionEvent(build, promotion.name, version)
        // Gets all the trigger configurations about this event, based on project & promotion only
        val branches = autoVersioningConfigurationService.getBranchesConfiguredFor(build.project.name, promotion.name)
        logger.debug("Raw configured branches for ${build.entityDisplayName} @ ${promotion.name}: ${branches.size}")
        // Cache for the project last source branches
        val cache = mutableMapOf<Pair<Int, String>, Branch?>()
        // Filters the branches based on their configuration and the triggering event
        val configuredBranches = branches.mapNotNull {
            filterBranch(it, promotionEvent, cache)
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
            promotionEvent
        )
    }

    private fun filterBranch(
        branch: Branch,
        promotionEvent: PromotionEvent,
        cache: MutableMap<Pair<Int, String>, Branch?>,
    ): AutoVersioningConfiguredBranch? {
        logger.debug("Filtering branch ${branch.entityDisplayName} for event $promotionEvent...")
        // The project must be configured for a SCM
        if (scmDetector.getSCM(branch.project) != null) {
            // Gets the auto versioning configuration for the branch
            val config = autoVersioningConfigurationService.getAutoVersioning(branch)
            if (config != null) {
                logger.debug("Auto versioning config present. Filtering the configurations...")
                // If present, gets its configurations
                val autoVersioningConfigurations = config.configurations
                    // Filters the configurations based on the event
                    .filter { configuration -> promotionEvent.match(configuration, cache) }
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

    private fun PromotionEvent.match(
        config: AutoVersioningSourceConfig,
        cache: MutableMap<Pair<Int, String>, Branch?>,
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
                config.sourcePromotion == promotion &&
                sourceBranchMatch(config, cache)
        if (logger.isDebugEnabled) {
            logger.debug("Promotion event matching: $match")
        }
        return match
    }

    private fun PromotionEvent.sourceBranchMatch(
        config: AutoVersioningSourceConfig,
        cache: MutableMap<Pair<Int, String>, Branch?>,
    ): Boolean {
        val latestSourceBranch = cache.getOrPut(build.projectId() to config.sourceBranch) {
            logger.debug("""Promotion event matching based on branch latest promotion""")
            autoVersioningConfigurationService.getLatestBranch(build.project, config)
        }
        logger.debug("Latest branch: ${latestSourceBranch?.name}")
        // We want the promoted build to be on the latest source branch
        return latestSourceBranch != null && latestSourceBranch.id() == build.branch.id()
    }

}