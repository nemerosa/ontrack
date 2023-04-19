package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditStoreConstants.STORE_CATEGORY
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Auto versioning audit store based on the entity data store of the source project.
 */
@Component
class AutoVersioningAuditStoreImpl(
        private val entityDataStore: EntityDataStore,
        private val helper: AutoVersioningAuditStoreHelper,
        private val securityService: SecurityService,
) : AutoVersioningAuditStore {

    private val logger: Logger = LoggerFactory.getLogger(AutoVersioningAuditStoreImpl::class.java)

    /**
     * Made accessible for tests
     */
    internal var signatureProvider: () -> Signature = { securityService.currentSignature }

    private fun signature() = signatureProvider()

    override fun cancelQueuedOrders(order: AutoVersioningOrder) {
        /**
         * Gets all processing orders:
         *
         * * from the same source than the order
         * * with the same target than the order
         * * whose state is running and not processing
         */
        val filter = AutoVersioningAuditQueryFilter(
                source = order.sourceProject,
                project = order.branch.project.name,
                branch = order.branch.name,
                states = AutoVersioningAuditState.runningAndNotProcessingStates,
        )
        findByFilter(filter).forEach { entry ->
            logger.debug("Cancelling $entry")
            addState(
                    targetBranch = entry.order.branch,
                    uuid = entry.order.uuid,
                    queue = null,
                    state = AutoVersioningAuditState.PROCESSING_CANCELLED,
            )
        }
    }

    override fun create(order: AutoVersioningOrder, routing: String) {
        TODO("Will be removed")
    }

    override fun addState(
            targetBranch: Branch,
            uuid: String,
            queue: String?,
            state: AutoVersioningAuditState,
            vararg data: Pair<String, String>,
    ) {
        TODO("Will be removed")
    }

    override fun findByUUID(targetBranch: Branch, uuid: String): AutoVersioningAuditEntry? {
        return entityDataStore.getByFilter(
                EntityDataStoreFilter(entity = targetBranch, category = STORE_CATEGORY, name = uuid)
        ).firstOrNull()?.toEntry()
    }

    override fun findByFilter(filter: AutoVersioningAuditQueryFilter): List<AutoVersioningAuditEntry> {
        return helper.auditVersioningEntries(filter)
    }

    override fun countByFilter(filter: AutoVersioningAuditQueryFilter): Int {
        return helper.auditVersioningEntriesCount(filter)
    }

    override fun removeAllBefore(retentionDate: LocalDateTime, nonRunningOnly: Boolean): Int =
            entityDataStore.deleteByFilter(
                    EntityDataStoreFilter(
                            category = STORE_CATEGORY,
                            beforeTime = retentionDate
                    ).withJsonFilter(
                            "json::jsonb->>'running' = :running",
                            "running" to (!nonRunningOnly).toString()
                    )
            )

    override fun removeAll() {
        entityDataStore.deleteByCategoryBefore(STORE_CATEGORY, Time.now().plusDays(1))
    }

    private fun EntityDataStoreRecord.toEntry() =
            data.parse<AutoVersioningAuditStoreData>().run {
                AutoVersioningAuditEntry(
                        order = AutoVersioningOrder(
                                uuid = this@toEntry.name,
                                branch = this@toEntry.entity as Branch,
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
                        ),
                        audit = states,
                        routing = routing,
                        queue = queue,
                )
            }

}