package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.common.replaceGroup
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditService
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningTargetFileService
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.event.AutoVersioningEventService
import net.nemerosa.ontrack.extension.av.metrics.AutoVersioningMetricsService
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessing
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingNotFoundException
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingRegistry
import net.nemerosa.ontrack.extension.av.properties.FilePropertyType
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.extension.scm.service.uploadLines
import net.nemerosa.ontrack.model.structure.NameDescription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningProcessingServiceImpl(
    private val scmDetector: SCMDetector,
    private val autoVersioningTargetFileService: AutoVersioningTargetFileService,
    private val postProcessingRegistry: PostProcessingRegistry,
    private val autoVersioningAuditService: AutoVersioningAuditService,
    private val metrics: AutoVersioningMetricsService,
    private val autoVersioningEventService: AutoVersioningEventService,
    private val autoVersioningCompletionListeners: List<AutoVersioningCompletionListener>,
    private val autoVersioningTemplatingService: AutoVersioningTemplatingService,
) : AutoVersioningProcessingService {

    private val logger: Logger = LoggerFactory.getLogger(AutoVersioningProcessingServiceImpl::class.java)

    override fun process(order: AutoVersioningOrder): AutoVersioningProcessingOutcome {
        if (order.sourcePromotion == null) error("AV order source promotion cannot be null for new requests.")
        logger.debug("Processing auto versioning order: {}", order)
        autoVersioningAuditService.onProcessingStart(order)
        val branch = order.branch
        // Gets the SCM configuration for the project
        val scm = scmDetector.getSCM(branch.project)
        val scmBranch: String? = scm?.getSCMBranch(branch)
        if (scm != null && scmBranch != null) {
            // Gets the clone URL & name for the repository
            val repositoryURI = scm.repositoryURI
            val repository = scm.repository
            // Sanitization of the version (for the branch name)
            val sanitizedVersion: String = NameDescription.escapeName(order.targetVersion)
            // Name of the upgrade branch
            val upgradeBranch = AutoVersioningSourceConfig.getUpgradeBranch(
                upgradeBranchPattern = order.upgradeBranchPattern,
                project = order.sourceProject,
                version = sanitizedVersion,
                branch = scmBranch,
                promotion = order.sourcePromotion,
                paths = order.targetPaths,
                branchHash = true // Target branch name and other elements as a hash (to avoid too long names)
            )
            // Commit ID of the new branch (not null when target branch is actually created)
            val commitId: String by lazy {
                // Creates an upgrade branch and gets its commit ID
                try {
                    logger.debug(
                        "Processing auto versioning order creating branch {}: {}",
                        upgradeBranch,
                        order
                    )
                    // Audit
                    autoVersioningAuditService.onProcessingCreatingBranch(order, upgradeBranch)
                    // Creating the branch
                    scm.createBranch(scmBranch, upgradeBranch)
                } catch (e: Exception) {
                    autoVersioningEventService.sendError(order, "Failed to create branch $upgradeBranch", e)
                    throw e
                }
            }
            // Map of current versions per path
            val currentVersions = mutableMapOf<String,String>()
            // For each target path
            val targetPathUpdated: List<Boolean> = try {
                order.targetPaths.map { targetPath ->
                    // Gets the content of the target file
                    val lines = scm.download(scmBranch, targetPath, retryOnNotFound = true)
                        ?.toString(Charsets.UTF_8)
                        ?.lines()
                        ?: throw AutoVersioningNoContentException(scmBranch, targetPath)
                    // Gets the current version in this file
                    val currentVersion: String = autoVersioningTargetFileService.readVersion(order, lines)
                        ?: throw AutoVersioningVersionNotFoundException(targetPath)
                    // If different version
                    if (currentVersion != order.targetVersion) {
                        // Storing the version
                        currentVersions[targetPath] = currentVersion
                        // Changes the version
                        val updatedLines = order.replaceVersion(lines)
                        // Pushes the change to the branch
                        logger.debug(
                            "Processing auto versioning order pushing upgrade to {}/{}: {}",
                            upgradeBranch,
                            targetPath,
                            order
                        )
                        // Audit
                        autoVersioningAuditService.onProcessingUpdatingFile(order, upgradeBranch, targetPath)
                        // Uploads of the file content
                        scm.uploadLines(upgradeBranch, commitId, targetPath, updatedLines, message = order.getCommitMessage())
                        // Post processing
                        if (!order.postProcessing.isNullOrBlank()) {
                            // Gets the post processor
                            val postProcessing = postProcessingRegistry.getPostProcessingById<Any>(order.postProcessing)
                            // If processing cannot be found, we consider this an error
                            if (postProcessing == null) {
                                throw PostProcessingNotFoundException(order.postProcessing)
                            }
                            // Launching the post processing
                            else {
                                logger.debug("Processing auto versioning order launching post processing: {}", order)
                                autoVersioningAuditService.onPostProcessingStart(order, upgradeBranch)
                                measureAndLaunchPostProcessing(
                                    postProcessing,
                                    order,
                                    repositoryURI,
                                    repository,
                                    upgradeBranch,
                                    scm,
                                )
                                logger.debug("Processing auto versioning order end of post processing: {}", order)
                                autoVersioningAuditService.onPostProcessingEnd(order, upgradeBranch)
                            }
                        }
                        // Changed
                        true
                    } else {
                        // Not changed
                        false
                    }
                }
            } catch (e: Exception) {
                autoVersioningEventService.sendError(
                    order,
                    e.message?.takeIf { it.isNotBlank() } ?: "Issue while processing the change",
                    e
                )
                throw e
            }

            // If at least one path updated
            if (targetPathUpdated.any { it }) {

                // Creates a PR with auto approval
                try {
                    logger.debug("Processing auto versioning order creating PR: {}", order)
                    // Audit
                    autoVersioningAuditService.onPRCreating(order, upgradeBranch)
                    // PR title & message
                    val (prTitle, prBody) = autoVersioningTemplatingService.generatePRInfo(order, currentVersions)
                    // PR creation
                    val pr = scm.createPR(
                        from = upgradeBranch,
                        to = scmBranch,
                        title = prTitle,
                        // Change log as description?
                        description = prBody,
                        // Auto approval
                        autoApproval = order.autoApproval,
                        // Remote auto merge?
                        remoteAutoMerge = (order.autoApprovalMode == AutoApprovalMode.SCM),
                        // Commit message to use on merge
                        message = order.getCommitMessage(),
                        // List of reviewers
                        reviewers = order.reviewers,
                    )
                    // If auto approval mode = CLIENT and PR is not merged, we had a timeout
                    logger.debug("Processing auto versioning order end of PR process: {}", order)
                    val outcome = if (order.autoApproval) {
                        when (order.autoApprovalMode) {
                            AutoApprovalMode.SCM -> {
                                autoVersioningAuditService.onPRApproved(
                                    order = order,
                                    upgradeBranch = upgradeBranch,
                                    prName = pr.name,
                                    prLink = pr.link
                                )
                                autoVersioningEventService.sendSuccess(
                                    order,
                                    "Auto versioning PR has been created and approved. Its merge process will be done at SCM level.",
                                    pr
                                )
                                // OK
                                AutoVersioningProcessingOutcome.CREATED
                            }
                            AutoApprovalMode.CLIENT -> if (!pr.merged) {
                                logger.debug("Processing auto versioning order PR timed out: {}", order)
                                // Audit
                                autoVersioningAuditService.onPRTimeout(
                                    order = order,
                                    upgradeBranch = upgradeBranch,
                                    prName = pr.name,
                                    prLink = pr.link
                                )
                                // Notification
                                autoVersioningEventService.sendPRMergeTimeoutError(
                                    order,
                                    pr = pr
                                )
                                // OK
                                AutoVersioningProcessingOutcome.TIMEOUT
                            } else {
                                autoVersioningAuditService.onPRMerged(
                                    order = order,
                                    upgradeBranch = upgradeBranch,
                                    prName = pr.name,
                                    prLink = pr.link
                                )
                                autoVersioningEventService.sendSuccess(
                                    order,
                                    "Auto versioning PR has been created, approved and merged.",
                                    pr
                                )
                                // OK
                                AutoVersioningProcessingOutcome.CREATED
                            }
                        }
                    } else {
                        autoVersioningAuditService.onPRCreated(
                            order = order,
                            upgradeBranch = upgradeBranch,
                            prName = pr.name,
                            prLink = pr.link
                        )
                        autoVersioningEventService.sendSuccess(
                            order,
                            "Auto versioning PR has been created.",
                            pr
                        )
                        // OK
                        AutoVersioningProcessingOutcome.CREATED
                    }
                    // Back validation
                    onCompletion(order, outcome)
                    // OK
                    return outcome
                } catch (e: Exception) {
                    autoVersioningEventService.sendError(order, "Failed to create PR", e)
                    throw e
                }

            } else {
                logger.debug("Processing auto versioning order same version: {}", order)
                autoVersioningAuditService.onProcessingAborted(order, "Same version")
                return AutoVersioningProcessingOutcome.SAME_VERSION
            }
        } else {
            logger.debug("Processing auto versioning order no config: {}", order)
            autoVersioningAuditService.onProcessingAborted(order, "Target branch is not configured")
            return AutoVersioningProcessingOutcome.NO_CONFIG
        }
    }

    private fun onCompletion(order: AutoVersioningOrder, outcome: AutoVersioningProcessingOutcome) {
        autoVersioningCompletionListeners.forEach { autoVersioningCompletionListener ->
            autoVersioningCompletionListener.onAutoVersioningCompletion(order, outcome)
        }
    }

    private fun <T> measureAndLaunchPostProcessing(
        postProcessing: PostProcessing<T>,
        order: AutoVersioningOrder,
        repositoryURI: String,
        repository: String,
        upgradeBranch: String,
        scm: SCM,
    ) {
        // Count
        metrics.onPostProcessingStarted(order, postProcessing)
        try {
            // Timing
            metrics.postProcessingTiming(order, postProcessing) {
                launchPostProcessing(
                    postProcessing,
                    order,
                    repositoryURI,
                    repository,
                    upgradeBranch,
                    scm,
                )
            }
            // Success
            metrics.onPostProcessingSuccess(order, postProcessing)
        } catch (ex: Exception) {
            // Metric for the error
            metrics.onPostProcessingError(order, postProcessing)
            // Going on with error processing
            throw ex
        }
    }

    private fun <T> launchPostProcessing(
        postProcessing: PostProcessing<T>,
        order: AutoVersioningOrder,
        repositoryURI: String,
        repository: String,
        upgradeBranch: String,
        scm: SCM,
    ) {
        // Parsing and validation of the configuration
        val config: T = postProcessing.parseAndValidate(order.postProcessingConfig)
        // Launching the post processing
        postProcessing.postProcessing(config, order, repositoryURI, repository, upgradeBranch, scm)
    }

    private fun AutoVersioningOrder.replaceVersion(content: List<String>): List<String> {
        val type = filePropertyType
        val actualValue = if (targetPropertyRegex.isNullOrBlank()) {
            targetVersion
        } else {
            val regex = targetPropertyRegex.toRegex()
            val previousValue = type.readProperty(content, targetProperty) ?: error("Cannot find target property")
            regex.replaceGroup(previousValue, 1, targetVersion)
        }
        return type.replaceProperty(content, targetProperty, actualValue)
    }

    private val AutoVersioningOrder.filePropertyType: FilePropertyType
        get() = autoVersioningTargetFileService.getFilePropertyType(this)

}