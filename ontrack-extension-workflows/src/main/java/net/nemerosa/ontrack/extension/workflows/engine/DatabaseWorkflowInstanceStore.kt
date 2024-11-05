package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.mgt.WorkflowSettings
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.model.tx.TransactionHelper
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConditionalOnProperty(
    prefix = "net.nemerosa.ontrack.extension.workflows",
    name = ["store"],
    havingValue = "database",
    matchIfMissing = true,
)
@Transactional
class DatabaseWorkflowInstanceStore(
    private val storageService: StorageService,
    private val cachedSettingsService: CachedSettingsService,
    private val transactionHelper: TransactionHelper,
) : WorkflowInstanceStore {

    companion object {
        private val STORE = WorkflowInstance::class.java.name
    }

    override fun store(instance: WorkflowInstance): WorkflowInstance =
        transactionHelper.inNewTransaction {
            storageService.store(STORE, instance.id, instance)
            storageService.find(STORE, instance.id, WorkflowInstance::class)
                ?: throw WorkflowInstanceNotFoundException(instance.id)
        }

    override fun cleanup() {
        val settings = cachedSettingsService.getCachedSettings(WorkflowSettings::class.java)
        val time = Time.now - Duration.of(settings.retentionDuration, ChronoUnit.MILLIS)
        storageService.deleteWithFilter(
            store = STORE,
            query = "data->>'timestamp' < :timestamp",
            queryVariables = mapOf("timestamp" to Time.store(time))
        )
    }

    override fun findById(id: String): WorkflowInstance? =
        transactionHelper.inNewTransactionNullable {
            storageService.find(STORE, id, WorkflowInstance::class)
        }

    override fun findByFilter(workflowInstanceFilter: WorkflowInstanceFilter): PaginatedList<WorkflowInstance> {

        val query = mutableListOf<String>()
        val queryVariables = mutableMapOf<String, Any?>()

        if (!workflowInstanceFilter.name.isNullOrBlank()) {
            query += "data->'workflow'->>'name' = :name"
            queryVariables["name"] = workflowInstanceFilter.name
        }

        return storageService.paginatedFilter(
            store = STORE,
            type = WorkflowInstance::class,
            offset = workflowInstanceFilter.offset,
            size = workflowInstanceFilter.size,
            query = query.joinToString(" AND ") { "( $it )" },
            queryVariables = queryVariables,
            orderQuery = "ORDER BY data::jsonb->>'id' DESC",
        )
    }

    override fun clearAll() {
        storageService.clear(STORE)
    }
}