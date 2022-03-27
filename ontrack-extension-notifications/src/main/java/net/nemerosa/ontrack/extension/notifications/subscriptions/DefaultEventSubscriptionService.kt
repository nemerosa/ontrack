package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.channels.getChannel
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.asJsonString
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.pagination.spanningPaginatedList
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
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
    val structureService: StructureService,
    val notificationChannelRegistry: NotificationChannelRegistry,
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
            // Checking the ACL
            securityService.checkProjectFunction(subscription.projectEntity, ProjectSubscriptionsWrite::class.java)
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
            if (securityService.isProjectFunctionGranted(projectEntity,
                    ProjectView::class.java) && securityService.isProjectFunctionGranted(projectEntity,
                    ProjectSubscriptionsRead::class.java)
            ) {
                entityDataStore.findLastByCategoryAndName(projectEntity, ENTITY_DATA_STORE_CATEGORY, id, null)
                    .getOrNull()
                    ?.let { record ->
                        fromRecord(projectEntity, record)?.data
                    }
            } else {
                null
            }
        } else {
            TODO("Checks also for global listeners")
        }

    override fun filterSubscriptions(filter: EventSubscriptionFilter): PaginatedList<SavedEventSubscription> =
        if (filter.entity == null) {
            TODO("Global subscriptions only")
        } else {
            filterEntitySubscriptions(filter.entity, filter)
        }

    private fun filterEntitySubscriptions(
        projectEntityID: ProjectEntityID,
        filter: EventSubscriptionFilter,
    ): PaginatedList<SavedEventSubscription> {
        // Loads the target entity
        val projectEntity = projectEntityID.type.getEntityFn(structureService).apply(ID.of(projectEntityID.id))
        // Checking the rights
        securityService.checkProjectFunction(projectEntity, ProjectView::class.java)
        securityService.checkProjectFunction(projectEntity, ProjectSubscriptionsRead::class.java)
        // Gets the list of recursive parents to consider
        val chain = if (filter.recursive == true) {
            projectEntity.parents()
        } else {
            listOf(projectEntity)
        }

        return spanningPaginatedList(
            offset = filter.offset,
            size = filter.size,
            seeds = chain.map { entity ->
                { offset, size ->
                    // Creating the store filter
                    var storeFilter = EntityDataStoreFilter(
                        entity = entity,
                        category = ENTITY_DATA_STORE_CATEGORY,
                        offset = offset,
                        count = size,
                    )
                    // All JSON context
                    var jsonContextChannels = false
                    var jsonContextEvents = false
                    val jsonFilters = mutableListOf<String>()
                    val jsonCriteria = mutableMapOf<String, String>()
                    // Filter: channel
                    if (!filter.channel.isNullOrBlank()) {
                        jsonContextChannels = true
                        val json = mapOf("channel" to filter.channel).asJson().format()
                        jsonFilters += """channels::jsonb @> '$json'::jsonb"""

                        // Filter: channel config
                        if (!filter.channelConfig.isNullOrBlank()) {
                            val channel = notificationChannelRegistry.getChannel(filter.channel)
                            val criteria = channel.toSearchCriteria(filter.channelConfig)
                            val jsonConfig = mapOf("channelConfig" to criteria).asJson().format()
                            jsonFilters += """channels::jsonb @> '$jsonConfig'::jsonb"""
                        }
                    }
                    // Filter: event type
                    if (!filter.eventType.isNullOrBlank()) {
                        jsonContextEvents = true
                        jsonFilters += """events = :eventType"""
                        jsonCriteria["eventType"] = filter.eventType
                    }
                    // Filter: created before
                    if (filter.createdBefore != null) {
                        storeFilter = storeFilter.withBeforeTime(filter.createdBefore)
                    }
                    // Filter: creator
                    if (!filter.creator.isNullOrBlank()) {
                        storeFilter = storeFilter.withCreator(filter.creator)
                    }
                    // Json context
                    val jsonContextList = mutableListOf<String>()
                    if (jsonContextChannels) {
                        jsonContextList += "left join jsonb_array_elements_text(json::jsonb->'channels') as channels on true"
                    }
                    if (jsonContextEvents) {
                        jsonContextList += "left join jsonb_array_elements_text(json::jsonb->'events') as events on true"
                    }
                    if (jsonContextList.isNotEmpty()) {
                        storeFilter = storeFilter.withJsonContext(jsonContextList.joinToString(" "))
                    }
                    // Json filter
                    storeFilter = storeFilter.withJsonFilter(
                        jsonFilters.joinToString(" AND ") { "( $it )" },
                        *jsonCriteria.toList().toTypedArray()
                    )
                    // Total count for THIS entity
                    val count = entityDataStore.getCountByFilter(storeFilter)
                    // Loading & converting the records
                    val items = entityDataStore.getByFilter(storeFilter).mapNotNull { record ->
                        fromRecord(entity, record)
                    }
                    // OK
                    count to items
                }
            }
        )
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
                        code(subscription.data)
                    }
                }
            }
        } else {
            // TODO Global registration not implemented yet
        }
    }

    private fun fromRecord(
        projectEntity: ProjectEntity?,
        record: EntityDataStoreRecord,
    ) = record.data.parseOrNull<SubscriptionRecord>()?.let {
        SavedEventSubscription(
            id = record.name,
            signature = record.signature,
            data = EventSubscription(
                it.channels,
                projectEntity,
                it.events,
            )
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