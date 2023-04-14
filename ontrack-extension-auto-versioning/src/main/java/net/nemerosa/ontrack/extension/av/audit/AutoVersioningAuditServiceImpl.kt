package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.recordings.RecordingsService
import net.nemerosa.ontrack.model.security.SecurityService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningAuditServiceImpl(
        private val autoVersioningRecordingsExtension: AutoVersioningRecordingsExtension,
        private val recordingsService: RecordingsService,
        private val securityService: SecurityService,
) : AutoVersioningAuditService {

    companion object {
        private const val MAX_STACK_HEIGHT = 20

        fun reducedStackTrace(error: Throwable) =
                ExceptionUtils.getStackFrames(error).take(MAX_STACK_HEIGHT).joinToString("\n")
    }

    override fun onQueuing(order: AutoVersioningOrder, routing: String, cancelling: Boolean) {
        val record = order.run {
            AutoVersioningAuditStoreData(
                    uuid = uuid,
                    sourceProject = sourceProject,
                    targetPaths = targetPaths,
                    targetRegex = targetRegex,
                    targetProperty = targetProperty,
                    targetPropertyRegex = targetPropertyRegex,
                    targetPropertyType = targetPropertyType,
                    targetVersion = targetVersion,
                    autoApproval = autoApproval,
                    upgradeBranchPattern = upgradeBranchPattern,
                    postProcessing = postProcessing,
                    postProcessingConfig = postProcessingConfig,
                    validationStamp = validationStamp,
                    autoApprovalMode = autoApprovalMode,
                    states = listOf(
                            AutoVersioningAuditEntryState(
                                    signature = securityService.currentSignature,
                                    state = AutoVersioningAuditState.CREATED,
                                    data = emptyMap()
                            )
                    ),
                    routing = routing,
                    queue = null,
            )
        }
        recordingsService.record(autoVersioningRecordingsExtension, record)
    }

    private fun AutoVersioningAuditStoreData.withState(
            state: AutoVersioningAuditState,
            vararg data: Pair<String, String>,
    ): AutoVersioningAuditStoreData {
        val newState = AutoVersioningAuditEntryState(
                signature = securityService.currentSignature,
                state = state,
                data = data.toMap()
        )
        return addState(newState)
    }

    private fun AutoVersioningAuditStoreData.withQueue(queue: String) = AutoVersioningAuditStoreData(
            uuid = uuid,
            sourceProject = sourceProject,
            targetPaths = targetPaths,
            targetRegex = targetRegex,
            targetProperty = targetProperty,
            targetPropertyRegex = targetPropertyRegex,
            targetPropertyType = targetPropertyType,
            targetVersion = targetVersion,
            autoApproval = autoApproval,
            upgradeBranchPattern = upgradeBranchPattern,
            postProcessing = postProcessing,
            postProcessingConfig = postProcessingConfig,
            validationStamp = validationStamp,
            autoApprovalMode = autoApprovalMode,
            states = states,
            routing = routing,
            queue = queue,
    )

    override fun onReceived(order: AutoVersioningOrder, queue: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.RECEIVED, "queue" to queue).withQueue(queue)
        }
    }

    override fun onError(order: AutoVersioningOrder, error: Throwable) {
        val stack = reducedStackTrace(error)
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.ERROR, "error" to stack)
        }
    }

    override fun onProcessingStart(order: AutoVersioningOrder) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PROCESSING_START)
        }
    }

    override fun onProcessingAborted(order: AutoVersioningOrder, message: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PROCESSING_ABORTED, "message" to message)
        }
    }

    override fun onProcessingCreatingBranch(order: AutoVersioningOrder, upgradeBranch: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PROCESSING_CREATING_BRANCH, "branch" to upgradeBranch)
        }
    }

    override fun onProcessingUpdatingFile(order: AutoVersioningOrder, upgradeBranch: String, targetPath: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PROCESSING_UPDATING_FILE,
                    "branch" to upgradeBranch,
                    "path" to targetPath
            )
        }
    }

    override fun onPostProcessingStart(order: AutoVersioningOrder, upgradeBranch: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(
                    AutoVersioningAuditState.POST_PROCESSING_START,
                    "branch" to upgradeBranch,
            )
        }
    }

    override fun onPostProcessingEnd(order: AutoVersioningOrder, upgradeBranch: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(
                    AutoVersioningAuditState.POST_PROCESSING_END,
                    "branch" to upgradeBranch,
            )
        }
    }

    override fun onPRCreating(order: AutoVersioningOrder, upgradeBranch: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(
                    AutoVersioningAuditState.PR_CREATING,
                    "branch" to upgradeBranch,
            )
        }
    }

    override fun onPRTimeout(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PR_TIMEOUT,
                    AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
                    AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
                    AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
            )
        }
    }

    override fun onPRCreated(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PR_CREATED,
                    AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
                    AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
                    AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
            )
        }
    }

    override fun onPRApproved(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PR_APPROVED,
                    AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
                    AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
                    AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
            )
        }
    }

    override fun onPRMerged(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        recordingsService.updateRecord(autoVersioningRecordingsExtension, order.uuid) {
            it.withState(AutoVersioningAuditState.PR_MERGED,
                    AutoVersioningAuditEntryStateDataKeys.BRANCH to upgradeBranch,
                    AutoVersioningAuditEntryStateDataKeys.PR_NAME to prName,
                    AutoVersioningAuditEntryStateDataKeys.PR_LINK to prLink
            )
        }
    }
}