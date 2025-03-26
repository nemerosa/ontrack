package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Service
@Profile("!${RunProfile.UNIT_TEST}")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class AutoVersioningAuditServiceImpl(
    store: AutoVersioningAuditStore
) : AbstractAutoVersioningAuditService(store) {

    @PostConstruct
    fun logging() {
        logger.info("[auto-versioning] Using production auto versioning audit service")
    }

    override fun onQueuing(order: AutoVersioningOrder, routing: String) {
        super.onQueuing(order, routing)
    }

    override fun onReceived(order: AutoVersioningOrder, queue: String) {
        super.onReceived(order, queue)
    }

    override fun onError(order: AutoVersioningOrder, error: Throwable) {
        super.onError(order, error)
    }

    override fun onProcessingStart(order: AutoVersioningOrder) {
        super.onProcessingStart(order)
    }

    override fun onProcessingAborted(order: AutoVersioningOrder, message: String) {
        super.onProcessingAborted(order, message)
    }

    override fun onProcessingCreatingBranch(order: AutoVersioningOrder, upgradeBranch: String) {
        super.onProcessingCreatingBranch(order, upgradeBranch)
    }

    override fun onProcessingUpdatingFile(order: AutoVersioningOrder, upgradeBranch: String, targetPath: String) {
        super.onProcessingUpdatingFile(order, upgradeBranch, targetPath)
    }

    override fun onPostProcessingStart(order: AutoVersioningOrder, upgradeBranch: String) {
        super.onPostProcessingStart(order, upgradeBranch)
    }

    override fun onPostProcessingEnd(order: AutoVersioningOrder, upgradeBranch: String) {
        super.onPostProcessingEnd(order, upgradeBranch)
    }

    override fun onPRCreating(order: AutoVersioningOrder, upgradeBranch: String) {
        super.onPRCreating(order, upgradeBranch)
    }

    override fun onPRTimeout(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        super.onPRTimeout(order, upgradeBranch, prName, prLink)
    }

    override fun onPRCreated(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        super.onPRCreated(order, upgradeBranch, prName, prLink)
    }

    override fun onPRApproved(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        super.onPRApproved(order, upgradeBranch, prName, prLink)
    }

    override fun onPRMerged(order: AutoVersioningOrder, upgradeBranch: String, prName: String, prLink: String) {
        super.onPRMerged(order, upgradeBranch, prName, prLink)
    }
}