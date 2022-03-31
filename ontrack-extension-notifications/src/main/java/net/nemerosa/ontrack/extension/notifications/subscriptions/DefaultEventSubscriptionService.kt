package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.channels.getChannel
import net.nemerosa.ontrack.json.*
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
    private val storageService: StorageService,
    private val entityDataStore: EntityDataStore,
    private val securityService: SecurityService,
    private val structureService: StructureService,
    private val notificationChannelRegistry: NotificationChannelRegistry,
) : EventSubscriptionService {

    companion object {
        private val ENTITY_STORE = EventSubscription::class.java.name
        private val GLOBAL_STORE = EventSubscription::class.java.name
    }

    override fun subscribe(subscription: EventSubscription): SavedEventSubscription {
        // Record to save
        val record = subscription.toSubscriptionRecord()
        return if (subscription.projectEntity != null) {
            // Checking the ACL
            securityService.checkProjectFunction(subscription.projectEntity, ProjectSubscriptionsWrite::class.java)
            // Tries to find any matching subscription
            val filter = EntityDataStoreFilter(
                entity = subscription.projectEntity,
                category = ENTITY_STORE,
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
                SavedEventSubscription(
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
                    ENTITY_STORE,
                    id,
                    securityService.currentSignature,
                    null,
                    record
                )
                // OK
                SavedEventSubscription(id, newRecord.signature, subscription)
            }
        } else {
            securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
            // Gets any existing subscription
            val existingRecordJson = record.asJson().format()
            val existingRecords = storageService.filterRecords(
                store = GLOBAL_STORE,
                type = SignedSubscriptionRecord::class,
                query = """data::jsonb @> '$existingRecordJson'"""
            )
            // One existing record, we just return it
            if (existingRecords.size == 1) {
                val (id, existingRecord) = existingRecords.toList().first()
                SavedEventSubscription(
                    id = id,
                    signature = existingRecord.signature,
                    data = subscription,
                )
            } else {
                // If more than 1 record, this is abnormal, we remove the old records
                existingRecords.forEach { (id, _) ->
                    storageService.delete(GLOBAL_STORE, id)
                }
                // New ID
                val id = UUID.randomUUID().toString()
                // Saves the subscription
                val storedRecord = SignedSubscriptionRecord(
                    signature = securityService.currentSignature,
                    channel = subscription.channel,
                    channelConfig = subscription.channelConfig,
                    events = subscription.events,
                    keywords = subscription.keywords,
                    disabled = subscription.disabled,
                )
                storageService.store(
                    GLOBAL_STORE,
                    id,
                    storedRecord
                )
                // OK
                SavedEventSubscription(id, storedRecord.signature, subscription)
            }
        }
    }

    private fun EventSubscription.toSubscriptionRecord() = SubscriptionRecord(
        channel,
        channelConfig,
        events,
        keywords,
        disabled,
    )

    override fun findSubscriptionById(projectEntity: ProjectEntity?, id: String): EventSubscription? =
        if (projectEntity != null) {
            if (securityService.isProjectFunctionGranted(projectEntity,
                    ProjectView::class.java) && securityService.isProjectFunctionGranted(projectEntity,
                    ProjectSubscriptionsRead::class.java)
            ) {
                entityDataStore.findLastByCategoryAndName(projectEntity, ENTITY_STORE, id, null)
                    .getOrNull()
                    ?.let { record ->
                        fromRecord(projectEntity, record)?.data
                    }
            } else {
                null
            }
        } else if (!securityService.isGlobalFunctionGranted(GlobalSubscriptionsManage::class.java)) {
            null
        } else {
            storageService.find(GLOBAL_STORE, id, SignedSubscriptionRecord::class)?.toEventSubscription()
        }

    private fun SignedSubscriptionRecord.toEventSubscription() = EventSubscription(
        channel = channel,
        channelConfig = channelConfig,
        projectEntity = null,
        events = events.toSet(),
        keywords = keywords,
        disabled = disabled,
    )

    override fun deleteSubscriptionById(projectEntity: ProjectEntity?, id: String) {
        if (projectEntity != null) {
            securityService.checkProjectFunction(projectEntity, ProjectView::class.java)
            securityService.checkProjectFunction(projectEntity, ProjectSubscriptionsWrite::class.java)
            entityDataStore.deleteByName(projectEntity, ENTITY_STORE, id)
        } else {
            securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
            storageService.delete(GLOBAL_STORE, id)
        }
    }

    override fun disableSubscriptionById(projectEntity: ProjectEntity?, id: String): SavedEventSubscription =
        disableSubscriptionById(projectEntity, id, disabled = true)

    override fun enableSubscriptionById(projectEntity: ProjectEntity?, id: String): SavedEventSubscription =
        disableSubscriptionById(projectEntity, id, disabled = false)

    private fun disableSubscriptionById(
        projectEntity: ProjectEntity?,
        id: String,
        disabled: Boolean,
    ): SavedEventSubscription =
        if (projectEntity != null) {
            securityService.checkProjectFunction(projectEntity, ProjectView::class.java)
            securityService.checkProjectFunction(projectEntity, ProjectSubscriptionsWrite::class.java)
            val record = findSubscriptionById(projectEntity, id)
                ?.disabled(disabled)
                ?: throw EventSubscriptionIdNotFoundException(null, id)
            val signature = securityService.currentSignature
            entityDataStore.replaceOrAddObject(
                projectEntity,
                ENTITY_STORE,
                id,
                signature,
                null,
                record.toSubscriptionRecord()
            )
            SavedEventSubscription(
                id,
                signature,
                record
            )
        } else {
            securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
            val record = storageService.find(GLOBAL_STORE, id, SignedSubscriptionRecord::class)
                ?.disabled(disabled)
                ?: throw EventSubscriptionIdNotFoundException(null, id)
            storageService.store(GLOBAL_STORE, id, record)
            SavedEventSubscription(
                id,
                record.signature,
                record.toEventSubscription()
            )
        }

    override fun filterSubscriptions(filter: EventSubscriptionFilter): PaginatedList<SavedEventSubscription> =
        if (filter.entity == null) {
            filterGlobalSubscriptions(filter)
        } else {
            filterEntitySubscriptions(filter.entity, filter)
        }

    private fun filterGlobalSubscriptions(
        filter: EventSubscriptionFilter,
    ): PaginatedList<SavedEventSubscription> {
        securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
        val (total, items) = filterGlobal(filter)(filter.offset, filter.size)
        return PaginatedList.create(items, filter.offset, filter.size, total)
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

        // Gets the list of recursive parents & global scope to consider
        val seeds = if (filter.recursive == true) {
            val entitySeeds = projectEntity.parents().map { entity -> filterEntity(entity, filter) }
            if (securityService.isGlobalFunctionGranted(GlobalSubscriptionsManage::class.java)) {
                entitySeeds + filterGlobal(filter)
            } else {
                entitySeeds
            }
        } else {
            listOf(
                filterEntity(projectEntity, filter)
            )
        }

        return spanningPaginatedList(
            offset = filter.offset,
            size = filter.size,
            seeds = seeds,
        )
    }

    private fun filterGlobal(
        filter: EventSubscriptionFilter,
    ): (Int, Int) -> Pair<Int, List<SavedEventSubscription>> = { offset: Int, size: Int ->
        // Query contexts & criteria
        val contextList = mutableListOf<String>()
        val jsonFilters = mutableListOf<String>()
        val jsonCriteria = mutableMapOf<String, String>()

        // Filter: channel
        if (!filter.channel.isNullOrBlank()) {
            jsonFilters += """data::jsonb->>'channel' = :channel"""
            jsonCriteria["channel"] = filter.channel
            // Filter: channel config
            if (!filter.channelConfig.isNullOrBlank()) {
                val channel = notificationChannelRegistry.getChannel(filter.channel)
                val criteria = channel.toSearchCriteria(filter.channelConfig).format()
                jsonFilters += """data::jsonb->'channelConfig' @> '$criteria'"""
            }
        }

        // Filter: event type
        if (!filter.eventType.isNullOrBlank()) {
            contextList += "left join jsonb_array_elements_text(data::jsonb->'events') as events on true"
            jsonFilters += """events = :eventType"""
            jsonCriteria["eventType"] = filter.eventType
        }
        // Filter: created before
        if (filter.createdBefore != null) {
            jsonFilters += """data::jsonb->'signature'->>'time' <= :time"""
            jsonCriteria["time"] = Time.store(filter.createdBefore)
        }
        // Filter: creator
        if (filter.creator != null) {
            jsonFilters += """data::jsonb->'signature'->'user'->>'name' = :creator"""
            jsonCriteria["creator"] = filter.creator
        }

        // Final context & criteria
        val context = contextList.joinToString(" ")
        val jsonFilter = jsonFilters.joinToString(" AND ") { "( $it )" }

        // Counts the total
        val total = storageService.count(
            store = GLOBAL_STORE,
            context = context,
            query = jsonFilter,
            queryVariables = jsonCriteria,
        )
        // Gets the items
        val items = storageService.filterRecords(
            store = GLOBAL_STORE,
            type = SignedSubscriptionRecord::class,
            context = context,
            query = jsonFilter,
            queryVariables = jsonCriteria,
            offset = offset,
            size = size,
        ).map { (id, record) ->
            SavedEventSubscription(
                id = id,
                signature = record.signature,
                data = EventSubscription(
                    channel = record.channel,
                    channelConfig = record.channelConfig,
                    projectEntity = null,
                    events = record.events.toSet(),
                    keywords = record.keywords,
                    disabled = record.disabled,
                )
            )
        }
        // OK
        total to items
    }

    private fun filterEntity(
        entity: ProjectEntity,
        filter: EventSubscriptionFilter,
    ): (Int, Int) -> Pair<Int, List<SavedEventSubscription>> = { offset: Int, size: Int ->
        // Creating the store filter
        var storeFilter = EntityDataStoreFilter(
            entity = entity,
            category = ENTITY_STORE,
            offset = offset,
            count = size,
        )
        // All JSON context
        var jsonContextEvents = false
        val jsonFilters = mutableListOf<String>()
        val jsonCriteria = mutableMapOf<String, String>()
        // Filter: channel
        if (!filter.channel.isNullOrBlank()) {
            jsonFilters += """json::jsonb->>'channel' = :channel"""
            jsonCriteria["channel"] = filter.channel
            // Filter: channel config
            if (!filter.channelConfig.isNullOrBlank()) {
                val channel = notificationChannelRegistry.getChannel(filter.channel)
                val criteria = channel.toSearchCriteria(filter.channelConfig).format()
                jsonFilters += """json::jsonb->'channelConfig' @> '$criteria'::jsonb"""
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

    override fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit) {
        if (event.entities.isNotEmpty()) {
            event.entities.values.forEach { projectEntity ->
                val filter = EntityDataStoreFilter(
                    entity = projectEntity,
                    category = ENTITY_STORE,
                    jsonContext = "left join jsonb_array_elements_text(json::jsonb->'events') as events on true",
                    jsonFilter = "events = :event",
                    jsonFilterCriterias = mapOf(
                        "event" to event.eventType.id
                    )
                )
                entityDataStore.forEachByFilter(filter) { storeRecord ->
                    val subscription = fromRecord(projectEntity, storeRecord)
                    if (subscription != null && event.matchesKeywords(subscription.data.keywords)) {
                        code(subscription.data)
                    }
                }
            }
        }
        // Getting the global subscriptions
        storageService.forEach(
            store = GLOBAL_STORE,
            type = SignedSubscriptionRecord::class,
            context = "left join jsonb_array_elements_text(data::jsonb->'events') as events on true",
            query = "events = :event",
            queryVariables = mapOf("event" to event.eventType.id)
        ) { key, record ->
            val subscription = SavedEventSubscription(
                id = key,
                signature = record.signature,
                data = EventSubscription(
                    channel = record.channel,
                    channelConfig = record.channelConfig,
                    projectEntity = null,
                    events = record.events.toSet(),
                    keywords = record.keywords,
                    disabled = record.disabled,
                )
            )
            if (event.matchesKeywords(subscription.data.keywords)) {
                code(subscription.data)
            }
        }
    }

    override fun removeAllGlobal() {
        securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
        storageService.deleteWithFilter(GLOBAL_STORE)
    }

    private fun fromRecord(
        projectEntity: ProjectEntity?,
        record: EntityDataStoreRecord,
    ) = record.data.parseOrNull<SubscriptionRecord>()?.let {
        SavedEventSubscription(
            id = record.name,
            signature = record.signature,
            data = EventSubscription(
                channel = it.channel,
                channelConfig = it.channelConfig,
                projectEntity = projectEntity,
                events = it.events.toSet(),
                keywords = it.keywords,
                disabled = it.disabled,
            )
        )
    }

    /**
     * Subscription record
     */
    data class SubscriptionRecord(
        val channel: String,
        val channelConfig: JsonNode,
        val events: Set<String>,
        val keywords: String?,
        val disabled: Boolean,
    )

    /**
     * Signed subscription record (for global storage)
     */
    data class SignedSubscriptionRecord(
        val signature: Signature,
        val channel: String,
        val channelConfig: JsonNode,
        val events: Set<String>,
        val keywords: String?,
        val disabled: Boolean,
    ) {
        fun toSubscriptionRecord() = SubscriptionRecord(channel, channelConfig, events, keywords, disabled)
        fun disabled(disabled: Boolean) =
            SignedSubscriptionRecord(signature, channel, channelConfig, events, keywords, disabled)
    }

}