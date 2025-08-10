package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.reducedStackTrace
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractAutoVersioningAuditService(
    private val store: AutoVersioningAuditStore,
) : AutoVersioningAuditService {

    protected val logger: Logger = LoggerFactory.getLogger(AutoVersioningAuditService::class.java)

    override fun cancelQueuedOrders(order: AutoVersioningOrder) {
        store.cancelQueuedOrders(order)
    }

    override fun onQueuing(order: AutoVersioningOrder, routing: String) {
        store.create(order, routing)
    }

    override fun onReceived(order: AutoVersioningOrder, queue: String) {
        store.addState(order.branch, order.uuid, queue, null, AutoVersioningAuditState.RECEIVED)
    }

    override fun onError(order: AutoVersioningOrder, error: Throwable) {
        val stack = reducedStackTrace(error)
        store.addState(
            order.branch,
            order.uuid,
            null,
            null,
            AutoVersioningAuditState.ERROR,
            "message" to (error.message ?: error::class.java.name),
            "error" to stack
        )
    }

    override fun onProcessingStart(order: AutoVersioningOrder) {
        store.addState(order.branch, order.uuid, null, null, AutoVersioningAuditState.PROCESSING_START)
    }

    override fun onProcessingAborted(order: AutoVersioningOrder, message: String) {
        store.addState(
            order.branch,
            order.uuid,
            null,
            null,
            AutoVersioningAuditState.PROCESSING_ABORTED,
            "message" to message
        )
    }

    override fun onProcessingCreatingBranch(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(
            targetBranch = order.branch,
            uuid = order.uuid,
            queue = null,
            upgradeBranch = upgradeBranch,
            state = AutoVersioningAuditState.PROCESSING_CREATING_BRANCH,
            "branch" to upgradeBranch
        )
    }

    override fun onProcessingUpdatingFile(order: AutoVersioningOrder, upgradeBranch: String, targetPath: String) {
        store.addState(
            order.branch, order.uuid,
            null,
            null,
            AutoVersioningAuditState.PROCESSING_UPDATING_FILE,
            "branch" to upgradeBranch,
            "path" to targetPath
        )
    }

    override fun onPostProcessingStart(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(
            order.branch,
            order.uuid,
            null,
            null,
            AutoVersioningAuditState.POST_PROCESSING_START,
            "branch" to upgradeBranch
        )
    }

    override fun onPostProcessingLaunched(
        order: AutoVersioningOrder,
        postProcessingInfo: PostProcessingInfo
    ) {
        store.addState(
            targetBranch = order.branch,
            uuid = order.uuid,
            queue = null,
            upgradeBranch = null,
            state = AutoVersioningAuditState.POST_PROCESSING_LAUNCHED,
            data = postProcessingInfo.data.toList().toTypedArray()
        )
    }

    override fun onPostProcessingEnd(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(
            order.branch,
            order.uuid,
            null,
            null,
            AutoVersioningAuditState.POST_PROCESSING_END,
            "branch" to upgradeBranch
        )
    }

    override fun onPRCreating(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(
            order.branch,
            order.uuid,
            null,
            null,
            AutoVersioningAuditState.PR_CREATING,
            "branch" to upgradeBranch
        )
    }

    override fun onPRTimeout(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid,
            null,
            null,
            AutoVersioningAuditState.PR_TIMEOUT,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }

    override fun onPRCreated(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid,
            null,
            null,
            AutoVersioningAuditState.PR_CREATED,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }

    override fun onPRApproved(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid,
            null,
            null,
            AutoVersioningAuditState.PR_APPROVED,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }

    override fun onPRMerged(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid,
            null,
            null,
            AutoVersioningAuditState.PR_MERGED,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }
}
