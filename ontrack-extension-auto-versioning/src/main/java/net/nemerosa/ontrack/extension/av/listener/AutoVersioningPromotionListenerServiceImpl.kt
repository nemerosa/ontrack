package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.common.logTime
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.project.AutoVersioningProjectPropertyType
import net.nemerosa.ontrack.extension.av.tracking.*
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

    override fun getConfiguredBranches(
        promotionLevel: PromotionLevel,
        tracking: AutoVersioningTracking,
    ) {
        // Gets all the trigger configurations about this event, based on project & promotion only
        val branches = autoVersioningConfigurationService.getBranchesConfiguredFor(
            promotionLevel.project.name,
            promotionLevel.name
        )
        // Filters the branches based on their configuration and the triggering event
        val cache = mutableMapOf<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>()
        val configuredBranches = branches
            .flatMap { branch ->
                autoVersioningConfigurationService.getAutoVersioning(branch)
                    ?.configurations
                    ?.map { config ->
                        AutoVersioningConfiguredBranch(
                            branch = branch,
                            configuration = config
                        )
                    }
                    ?: emptyList()
            }
        // Initializing the tracking
        val branchTrails = tracking.init(configuredBranches)
        branchTrails.forEach { branchTrail ->
            val disabled = branchTrail.branch.isDisabled || branchTrail.branch.project.isDisabled
            if (disabled) {
                tracking.withDisabledBranch(branchTrail)
            } else {
                // Filtering on project rules
                val accepted = acceptBranchWithProjectAVRules(branchTrail.branch)
                if (!accepted) {
                    tracking.reject(branchTrail, "Not accepted by the project auto-versioning rules")
                } else {
                    // Filtering on configuration
                    filterBranch(branchTrail, promotionLevel, cache, tracking)
                }
            }
        }
    }

    private fun filterBranch(
        branchTrail: AutoVersioningBranchTrail,
        promotionLevel: PromotionLevel,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
        tracking: AutoVersioningTracking,
    ): Boolean {
        // The project must be configured for a SCM
        if (scmDetector.getSCM(branchTrail.branch.project) != null) {
            // Filters the configuration based on the event
            return match(branchTrail, promotionLevel, cache, tracking)
        }
        // No SCM configuration
        else {
            tracking.reject(branchTrail, "No supported SCM configured at project level")
            return false
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
        branchTrail: AutoVersioningBranchTrail,
        sourcePromotion: PromotionLevel,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
        tracking: AutoVersioningTracking,
    ): Boolean {
        if (branchTrail.configuration.sourceProject == sourcePromotion.project.name &&
            branchTrail.configuration.sourcePromotion == sourcePromotion.name
        ) {
            val match = logger.logTime("AV Source Branch match") {
                sourceBranchMatch(sourcePromotion.branch, branchTrail, cache, tracking)
            }
            return match
        } else {
            tracking.reject(branchTrail, "Project & promotion names not matching")
            return false
        }
    }

    private fun sourceBranchMatch(
        sourceBranch: Branch,
        branchTrail: AutoVersioningBranchTrail,
        cache: MutableMap<LatestSourceBranchCacheKey, LatestSourceBranchCacheValue>,
        tracking: AutoVersioningTracking,
    ): Boolean {
        val cacheKey = LatestSourceBranchCacheKey(branchTrail.branch, sourceBranch.project, branchTrail.configuration)
        val latestSourceBranch = cache.getOrPut(cacheKey) {
            LatestSourceBranchCacheValue(
                autoVersioningConfigurationService.getLatestBranch(
                    branchTrail.branch,
                    sourceBranch.project,
                    branchTrail.configuration
                )
            )
        }.branch
        // We want the promoted build to be on the latest source branch
        val branchMatching = latestSourceBranch != null && latestSourceBranch.id() == sourceBranch.id()
        if (!branchMatching) {
            tracking.withNotLatestBranch(branchTrail, latestSourceBranch)
        }
        return branchMatching
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