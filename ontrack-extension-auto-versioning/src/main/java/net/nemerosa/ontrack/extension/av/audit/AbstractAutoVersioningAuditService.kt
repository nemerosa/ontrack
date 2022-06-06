package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractAutoVersioningAuditService(
    private val store: AutoVersioningAuditStore,
) : AutoVersioningAuditService {

    protected val logger: Logger = LoggerFactory.getLogger(AutoVersioningAuditService::class.java)

    companion object {
        private const val MAX_STACK_HEIGHT = 20

        fun reducedStackTrace(error: Throwable) =
            ExceptionUtils.getStackFrames(error).take(MAX_STACK_HEIGHT).joinToString("\n")
    }

    override fun onQueuing(order: AutoVersioningOrder) {
        store.create(order)
    }

    override fun onReceived(order: AutoVersioningOrder) {
        store.addState(order.branch, order.uuid, AutoVersioningAuditState.RECEIVED)
    }

    override fun onError(order: AutoVersioningOrder, error: Throwable) {
        val stack = reducedStackTrace(error)
        store.addState(
            order.branch,
            order.uuid,
            AutoVersioningAuditState.ERROR,
            "error" to stack
        )
    }

    override fun onProcessingStart(order: AutoVersioningOrder) {
        store.addState(order.branch, order.uuid, AutoVersioningAuditState.PROCESSING_START)
    }

    override fun onProcessingAborted(order: AutoVersioningOrder, message: String) {
        store.addState(order.branch, order.uuid, AutoVersioningAuditState.PROCESSING_ABORTED, "message" to message)
    }

    override fun onProcessingCreatingBranch(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(
            order.branch,
            order.uuid,
            AutoVersioningAuditState.PROCESSING_CREATING_BRANCH,
            "branch" to upgradeBranch
        )
    }

    override fun onProcessingUpdatingFile(order: AutoVersioningOrder, upgradeBranch: String, targetPath: String) {
        store.addState(
            order.branch, order.uuid,
            AutoVersioningAuditState.PROCESSING_UPDATING_FILE,
            "branch" to upgradeBranch,
            "path" to targetPath
        )
    }

    override fun onPostProcessingStart(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(
            order.branch,
            order.uuid,
            AutoVersioningAuditState.POST_PROCESSING_START,
            "branch" to upgradeBranch
        )
    }

    override fun onPostProcessingEnd(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(
            order.branch,
            order.uuid,
            AutoVersioningAuditState.POST_PROCESSING_END,
            "branch" to upgradeBranch
        )
    }

    override fun onPRCreating(order: AutoVersioningOrder, upgradeBranch: String) {
        store.addState(order.branch, order.uuid, AutoVersioningAuditState.PR_CREATING, "branch" to upgradeBranch)
    }

    override fun onPRTimeout(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid, AutoVersioningAuditState.PR_TIMEOUT,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }

    override fun onPRCreated(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid, AutoVersioningAuditState.PR_CREATED,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }

    override fun onPRApproved(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid, AutoVersioningAuditState.PR_APPROVED,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }

    override fun onPRMerged(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        store.addState(
            order.branch, order.uuid, AutoVersioningAuditState.PR_MERGED,
            AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
            AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
            AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
        )
    }
}