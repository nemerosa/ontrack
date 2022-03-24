package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.asJsonString
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
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
                return SavedEventSubscription(
                    records.first().name, subscription
                )
            } else {
                // If more than 1 record, this is abnormal, we remove the old records
                entityDataStore.deleteByFilter(filter)
                // New ID
                val id = UUID.randomUUID().toString()
                // Saves the subscription
                entityDataStore.replaceOrAddObject(
                    subscription.projectEntity,
                    ENTITY_DATA_STORE_CATEGORY,
                    id,
                    securityService.currentSignature,
                    null,
                    record
                )
                // OK
                return SavedEventSubscription(id, subscription)
            }
        } else {
            TODO("Global registration not implemented yet")
        }
    }

    override fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit) {
        if (event.entities.isNotEmpty()) {
            event.entities.values.forEach { projectEntity ->
                val filter = EntityDataStoreFilter(
                    entity = projectEntity,
                    category = ENTITY_DATA_STORE_CATEGORY,
                    // TODO Filter on event type
                    // left join jsonb_array_elements_text(json::jsonb->'events') as events on true
                    // events = 'NEW_PROMOTION_RUN'
                )
                entityDataStore.forEachByFilter(filter) { storeRecord ->
                    val record = storeRecord.data.parseOrNull<SubscriptionRecord>()
                    if (record != null) {
                        val subscription = EventSubscription(
                            record.channels,
                            projectEntity,
                            record.events,
                        )
                        code(subscription)
                    }
                }
            }
        } else {
            TODO("Global registration not implemented yet")
        }
    }

    /**
     * Subscription record
     */
    data class SubscriptionRecord(
        val channels: Set<EventSubscriptionChannel>,
        val events: Set<String>,
    )

}