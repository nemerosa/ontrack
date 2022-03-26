package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.asJsonString
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecord
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultEventSubscriptionService(
    val storageService: StorageService,
    val entityDataStore: EntityDataStore,
    val securityService: SecurityService,
) : EventSubscriptionService {

    companion object {
        private val ENTITY_DATA_STORE_CATEGORY = EventSubscription::class.java.name
    }

    override fun subscribe(subscription: EventSubscription): SavedEventSubscription {
        // TODO Checks the underlying entity is authorized
        // Record to save
        val record = SubscriptionRecord(
            subscription.channels,
            subscription.events,
        )
        if (subscription.projectEntity != null) {
            // Tries to find any matching subscription
            val filter = EntityDataStoreFilter(
                entity = subscription.projectEntity,
                category = ENTITY_DATA_STORE_CATEGORY,
                jsonFilter = "json::jsonb = CAST(:json AS JSONB)",
                jsonFilterCriterias = mapOf("json" to record.asJson().asJsonString()),
            )
            val records = entityDataStore.getByFilter(
                filter
            )
            // One existing record
            if (records.size == 1) {
                // Just returns the existing record
                val existingRecord = records.first()
                return SavedEventSubscription(
                    id = existingRecord.name,
                    signature = existingRecord.signature,
                    data = subscription,
                )
            } else {
                // If more than 1 record, this is abnormal, we remove the old records
                entityDataStore.deleteByFilter(filter)
                // New ID
                val id = UUID.randomUUID().toString()
                // Saves the subscription
                val newRecord = entityDataStore.replaceOrAddObject(
                    subscription.projectEntity,
                    ENTITY_DATA_STORE_CATEGORY,
                    id,
                    securityService.currentSignature,
                    null,
                    record
                )
                // OK
                return SavedEventSubscription(id, newRecord.signature, subscription)
            }
        } else {
            TODO("Global registration not implemented yet")
        }
    }

    override fun findSubscriptionById(projectEntity: ProjectEntity?, id: String): EventSubscription? =
        if (projectEntity != null) {
            entityDataStore.findLastByCategoryAndName(projectEntity, ENTITY_DATA_STORE_CATEGORY, id, null).getOrNull()
                ?.let { record ->
                    fromRecord(projectEntity, record)
                }
        } else {
            TODO("Checks also for global listeners")
        }

    override fun filterSubscriptions(filter: EventSubscriptionFilter): PaginatedList<SavedEventSubscription> {
        TODO("Not yet implemented")
    }

    override fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit) {
        if (event.entities.isNotEmpty()) {
            event.entities.values.forEach { projectEntity ->
                val filter = EntityDataStoreFilter(
                    entity = projectEntity,
                    category = ENTITY_DATA_STORE_CATEGORY,
                    jsonContext = "left join jsonb_array_elements_text(json::jsonb->'events') as events on true",
                    jsonFilter = "events = :event",
                    jsonFilterCriterias = mapOf(
                        "event" to event.eventType.id
                    )
                )
                entityDataStore.forEachByFilter(filter) { storeRecord ->
                    val subscription = fromRecord(projectEntity, storeRecord)
                    if (subscription != null) {
                        code(subscription)
                    }
                }
            }
        } else {
            TODO("Global registration not implemented yet")
        }
    }

    private fun fromRecord(
        projectEntity: ProjectEntity?,
        record: EntityDataStoreRecord,
    ) = record.data.parseOrNull<SubscriptionRecord>()?.let {
        EventSubscription(
            it.channels,
            projectEntity,
            it.events,
        )
    }

    /**
     * Subscription record
     */
    data class SubscriptionRecord(
        val channels: Set<EventSubscriptionChannel>,
        val events: Set<String>,
    )

}