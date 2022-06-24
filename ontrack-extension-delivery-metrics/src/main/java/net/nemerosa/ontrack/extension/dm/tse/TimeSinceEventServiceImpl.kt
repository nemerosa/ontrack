package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class TimeSinceEventServiceImpl(
    private val structureService: StructureService,
    private val buildFilterService: BuildFilterService,
    private val branchModelMatcherService: BranchModelMatcherService,
    private val metricsExportService: MetricsExportService,
    private val timeSincePromotionHelper: TimeSincePromotionHelper,
) : TimeSinceEventService {

    override fun collectTimesSinceEvents(project: Project, logger: (String) -> Unit) {
        // Branch model matcher for this project
        val branchModelMatcher = branchModelMatcherService.getBranchModelMatcher(project)
        // Gets all main branches
        val branches = structureService.getBranchesForProject(project.id)
            .filter { !it.isDisabled }
            .filter { branchModelMatcher?.matches(it) ?: false }
        // For each branch, collect the metrics
        branches.forEach { branch ->
            collectTimesSinceEvents(branch, logger)
        }
    }

    private fun collectTimesSinceEvents(branch: Branch, logger: (String) -> Unit) {
        logger("Collecting days since promotion for ${branch.entityDisplayName}")
        // Gets the last build for this branch
        val lastBuild = buildFilterService
            .standardFilterProviderData(1).build()
            .filterBranchBuilds(branch)
            .firstOrNull()
            ?: return // If no build, no collection
        // Gets the list of promotions for this branch
        val promotions = structureService.getPromotionLevelListForBranch(branch.id)
        // Collects times for each promotion
        promotions.forEach { promotion ->
            collectTimesSincePromotion(branch, lastBuild, promotion, logger)
        }
        // Gets the list of validations for this branch
        val validations = structureService.getValidationStampListForBranch(branch.id)
        // Collect times for each validation
        validations.forEach { validation ->
            collectTimesSinceValidation(branch, lastBuild, validation, logger)
        }
    }

    private fun collectTimesSincePromotion(
        branch: Branch,
        lastBuild: Build,
        promotion: PromotionLevel,
        logger: (String) -> Unit,
    ) {
        logger("Collecting times since ${promotion.name} promotion for ${branch.entityDisplayName}")
        // Gets the last build for this promotion
        val promotedBuild = buildFilterService.standardFilterProviderData(1)
            .withWithPromotionLevel(promotion.name)
            .build()
            .filterBranchBuilds(branch)
            .firstOrNull()
        // If there is a build, collect the data, if not, ignore it
        if (promotedBuild != null) {
            collectAbsoluteTimeSincePromotion(branch, lastBuild, promotedBuild, promotion)
            collectRelativeTimeSincePromotion(branch, promotedBuild, promotion)
        }
    }

    private fun collectAbsoluteTimeSincePromotion(
        branch: Branch,
        lastBuild: Build,
        promotedBuild: Build,
        promotion: PromotionLevel,
    ) {
        val now = Time.now()
        val hoursSince: Long = if (promotedBuild.id == lastBuild.id) {
            0 // Last build is promoted, always considered as 0
        } else {
            // Missing a promotion, starting to compute
            // Time of the promotion
            val run = structureService.getPromotionRunsForBuildAndPromotionLevel(promotedBuild, promotion).first()
            val promotionTime = run.signature.time
            // Difference in hours
            val diff = Duration.between(promotionTime, now)
            diff.toHours()
        }
        // Sends the metrics
        metricsExportService.exportMetrics(
            metric = TimeSinceEventMetrics.TIME_SINCE_PROMOTION,
            tags = mapOf(
                "project" to branch.project.name,
                "branch" to branch.name,
                "promotion" to promotion.name
            ),
            fields = mapOf(
                "hours" to hoursSince.toDouble(),
                "days" to (hoursSince / 24).toDouble()
            ),
            timestamp = now
        )
    }

    private fun collectRelativeTimeSincePromotion(branch: Branch, promotedBuild: Build, promotion: PromotionLevel) {
        // Time of the current promotion
        val promotionRun = structureService.getPromotionRunsForBuildAndPromotionLevel(promotedBuild, promotion)
            .firstOrNull()
        val hours = if (promotionRun != null) {
            // Gets the previous promotion in the branch model (if any)
            val allPromotions = structureService.getPromotionLevelListForBranch(branch.id)
            val promotionIndex = allPromotions.indexOfFirst { it.id() == promotion.id() }
            val previousPromotion = if (promotionIndex > 0) allPromotions[promotionIndex - 1] else null
            // Now, looking for the OLDEST event for the previous promotion (or build is not defined)
            // AFTER the current promotion
            val previousPromotionTime = if (previousPromotion == null) {
                // No previous promotion level, so we look for the OLDEST BUILD AFTER the current promotion
                val build = timeSincePromotionHelper.findOldestBuildAfterBuild(promotionRun.build)?.let {
                    structureService.getBuild(ID.of(it))
                }
                // Returns its creation time
                build?.signature?.time
            } else {
                val previousPromotionRun =
                    timeSincePromotionHelper.findOldestPromotionAfterBuild(promotionRun.build, previousPromotion)?.let {
                        structureService.getPromotionRun(ID.of(it))
                    }
                // Returns its creation time
                previousPromotionRun?.signature?.time
            }
            // Difference in hours
            if (previousPromotionTime != null) {
                val diff = Duration.between(previousPromotionTime, Time.now())
                diff.toHours()
            } else {
                0
            }
        } else {
            0
        }
        // Sends the metrics
        metricsExportService.exportMetrics(
            metric = TimeSinceEventMetrics.RELATIVE_TIME_SINCE_PROMOTION,
            tags = mapOf(
                "project" to branch.project.name,
                "branch" to branch.name,
                "promotion" to promotion.name
            ),
            fields = mapOf(
                "hours" to hours.toDouble(),
                "days" to (hours / 24).toDouble()
            ),
            timestamp = Time.now()
        )
    }

    private fun collectTimesSinceValidation(
        branch: Branch,
        lastBuild: Build,
        validation: ValidationStamp,
        logger: (String) -> Unit,
    ) {
        logger("Collecting times since ${validation.name} validation for ${branch.entityDisplayName}")
        // Gets the last build for this validation
        val validatedBuild = buildFilterService.standardFilterProviderData(1)
            .withWithValidationStamp(validation.name)
            .build()
            .filterBranchBuilds(branch)
            .firstOrNull()
        // Gets the last PASSED build for this validation
        val validatedPassedBuild = buildFilterService.standardFilterProviderData(1)
            .withWithValidationStamp(validation.name)
            .withWithValidationStampStatus(ValidationRunStatusID.PASSED)
            .build()
            .filterBranchBuilds(branch)
            .firstOrNull()
        // At least one validation to start collecting times
        if (validatedBuild != null) {
            val now = Time.now()
            // Metrics for last validation
            collectTimesSinceValidation(
                now,
                branch,
                lastBuild,
                validatedBuild,
                validation,
                TimeSinceEventMetrics.TIME_SINCE_VALIDATION,
                statusTag = true
            )
            // Metrics for last PASSED validation
            if (validatedPassedBuild != null) {
                collectTimesSinceValidation(
                    now,
                    branch,
                    lastBuild,
                    validatedPassedBuild,
                    validation,
                    TimeSinceEventMetrics.TIME_SINCE_PASSED_VALIDATION,
                    statusTag = false
                )
            }
        }
    }

    private fun collectTimesSinceValidation(
        now: LocalDateTime,
        branch: Branch,
        lastBuild: Build,
        validatedBuild: Build,
        validation: ValidationStamp,
        metricName: String,
        statusTag: Boolean,
    ) {
        // Last status
        val run: ValidationRunStatus = structureService.getValidationRunsForBuildAndValidationStamp(
            validatedBuild.id,
            validation.id,
            0, 1
        ).first().lastStatus

        val hoursSince: Long = if (validatedBuild.id == lastBuild.id) {
            0 // Last build is validated, always considered as 0
        } else {
            // Missing a validation, starting to compute
            val validationTime = run.signature.time
            // Different in days
            val diff = Duration.between(validationTime, now)
            diff.toHours()
        }
        // Tags
        val tags = mutableMapOf<String, String>(
            "project" to branch.project.name,
            "branch" to branch.name,
            "validation_stamp" to validation.name
        )
        // Status
        if (statusTag) {
            tags += "status" to run.statusID.id
        }
        // Sends the metrics
        metricsExportService.exportMetrics(
            metric = metricName,
            tags = tags,
            fields = mapOf(
                "hours" to hoursSince.toDouble(),
                "days" to (hoursSince / 24).toDouble()
            ),
            timestamp = now
        )
    }

}