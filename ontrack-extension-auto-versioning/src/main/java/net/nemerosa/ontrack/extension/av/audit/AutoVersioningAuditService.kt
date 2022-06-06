package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

/**
 * This service is responsible to log events throughout the whole life-cycle
 * of an auto versioning process.
 *
 * Those events include (non exhaustively):
 *
 * * the creation of an auto versioning process and its queing
 * * the start of its processing
 * * the post processing if any
 * * the PR creation and processing if any
 * * any error attached to this PR
 *
 * Throughout its processing, an auto versioning process is uniquely
 * identified by its [UUID][AutoVersioningOrder.uuid] and is linked to
 * the _target project_.
 */
interface AutoVersioningAuditService {

    /**
     * The [order] was just queued. It's the first event in the story of this auto versioning order.
     *
     * @param order Auto versioning order
     */
    fun onQueuing(order: AutoVersioningOrder)

    /**
     * The [order] was received on the queue and is ready for processing
     *
     * @param order Auto versioning order
     */
    fun onReceived(order: AutoVersioningOrder)

    /**
     * Marks the [order] has been in error.
     *
     * @param order Auto versioning order being processed
     * @param error The error on processing
     */
    fun onError(order: AutoVersioningOrder, error: Throwable)

    /**
     * The processing of this [order] has started.
     *
     * @param order Auto versioning order being processed
     */
    fun onProcessingStart(order: AutoVersioningOrder)

    /**
     * The processing of this [order] has been aborted.
     *
     * @param order Auto versioning order being processed
     * @param message Explanatory message
     */
    fun onProcessingAborted(order: AutoVersioningOrder, message: String)

    /**
     * Creating the upgrade branch for this [order].
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     */
    fun onProcessingCreatingBranch(order: AutoVersioningOrder, upgradeBranch: String)

    /**
     * File at path [targetPath] on branch [upgradeBranch] is being updated with
     * the new version for the [order] being processed.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     * @param targetPath Path to the file being updated
     */
    fun onProcessingUpdatingFile(order: AutoVersioningOrder, upgradeBranch: String, targetPath: String)

    /**
     * Post processing has started for the [order] for the [upgradeBranch] branch.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     */
    fun onPostProcessingStart(order: AutoVersioningOrder, upgradeBranch: String)

    /**
     * Post processing has finished for the [order] for the [upgradeBranch] branch.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     */
    fun onPostProcessingEnd(order: AutoVersioningOrder, upgradeBranch: String)

    /**
     * Creating the SCM PR for the [order] from the [upgradeBranch] branch.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     */
    fun onPRCreating(order: AutoVersioningOrder, upgradeBranch: String)

    /**
     * The SCM PR for the [order] from the [upgradeBranch] branch was created, but it could not
     * be merged in a timely fashion.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     * @param prName Name of the pull request having been created
     * @param prLink Link to the pull request having been created
     */
    fun onPRTimeout(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String)

    /**
     * The SCM PR for the [order] from the [upgradeBranch] branch has been created. This is the end state
     * when auto-approval is disabled.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     * @param prName Name of the pull request having been created
     * @param prLink Link to the pull request having been created
     */
    fun onPRCreated(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String)

    /**
     * The SCM PR for the [order] from the [upgradeBranch] branch has been created and approved. This is the end state
     * when auto-approval is enabled but merge is delegated to the SCM.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     * @param prName Name of the pull request having been created
     * @param prLink Link to the pull request having been created
     */
    fun onPRApproved(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String)

    /**
     * The SCM PR for the [order] from the [upgradeBranch] branch has been merged. This is the end state
     * when auto-approval is enabled and merge process is managed by Ontrack.
     *
     * @param order Auto versioning order being processed
     * @param upgradeBranch Actual name of the upgrade branch
     * @param prName Name of the pull request having been created
     * @param prLink Link to the pull request having been created
     */
    fun onPRMerged(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String)
}
