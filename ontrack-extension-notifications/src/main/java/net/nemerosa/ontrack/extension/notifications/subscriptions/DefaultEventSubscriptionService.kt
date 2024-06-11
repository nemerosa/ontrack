package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.pagination.spanningPaginatedList
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service

@Service
class DefaultEventSubscriptionService(
    private val globalSubscriptionStore: GlobalSubscriptionStore,
    private val entitySubscriptionStore: EntitySubscriptionStore,
    private val securityService: SecurityService,
    private val structureService: StructureService,
) : EventSubscriptionService {

    override fun subscribe(subscription: EventSubscription) {
        // Record to save
        val record = subscription.toSubscriptionRecord()
        if (subscription.projectEntity != null) {
            // Checking the ACL
            securityService.checkProjectFunction(subscription.projectEntity, ProjectSubscriptionsWrite::class.java)
            // Saving the subscription
            entitySubscriptionStore.save(
                entity = subscription.projectEntity,
                record = record,
            )
        } else {
            securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
            // Saving the subscription
            globalSubscriptionStore.save(record)
        }
    }

    private fun EventSubscription.toSubscriptionRecord() = SubscriptionRecord(
        name = name,
        channel = channel,
        channelConfig = channelConfig,
        events = events,
        keywords = keywords,
        disabled = disabled,
        origin = origin,
        contentTemplate = contentTemplate,
    )

    override fun findSubscriptionByName(projectEntity: ProjectEntity?, name: String): EventSubscription? =
        if (projectEntity != null) {
            if (securityService.isProjectFunctionGranted(
                    projectEntity,
                    ProjectView::class.java
                ) && securityService.isProjectFunctionGranted(
                    projectEntity,
                    ProjectSubscriptionsRead::class.java
                )
            ) {
                entitySubscriptionStore.findByName(projectEntity, name)?.let { record ->
                    entitySubscriptionRecordToSubscription(projectEntity, record)
                }
            } else {
                null
            }
        } else if (!securityService.isGlobalFunctionGranted(GlobalSubscriptionsManage::class.java)) {
            null
        } else {
            globalSubscriptionStore.find(name)?.let {
                subscriptionRecordToGlobalSubscription(it)
            }
        }

    override fun deleteSubscriptionByName(projectEntity: ProjectEntity?, name: String) {
        if (projectEntity != null) {
            securityService.checkProjectFunction(projectEntity, ProjectView::class.java)
            securityService.checkProjectFunction(projectEntity, ProjectSubscriptionsWrite::class.java)
            entitySubscriptionStore.deleteByName(projectEntity, name)
        } else {
            securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
            globalSubscriptionStore.delete(name)
        }
    }

    override fun deleteSubscriptionsByEntity(projectEntity: ProjectEntity) {
        securityService.checkProjectFunction(projectEntity, ProjectView::class.java)
        securityService.checkProjectFunction(projectEntity, ProjectSubscriptionsWrite::class.java)
        entitySubscriptionStore.deleteAll(projectEntity)
    }

    override fun deleteSubscriptionsByEntityAndOrigin(projectEntity: ProjectEntity, origin: String) {
        securityService.checkProjectFunction(projectEntity, ProjectView::class.java)
        securityService.checkProjectFunction(projectEntity, ProjectSubscriptionsWrite::class.java)
        entitySubscriptionStore.deleteByOrigin(projectEntity, origin)
    }

    override fun disableSubscriptionByName(projectEntity: ProjectEntity?, name: String) =
        disableSubscriptionByName(projectEntity, name, disabled = true)

    override fun enableSubscriptionByName(projectEntity: ProjectEntity?, name: String) =
        disableSubscriptionByName(projectEntity, name, disabled = false)

    private fun disableSubscriptionByName(
        projectEntity: ProjectEntity?,
        name: String,
        disabled: Boolean,
    ) {
        if (projectEntity != null) {
            securityService.checkProjectFunction(projectEntity, ProjectView::class.java)
            securityService.checkProjectFunction(projectEntity, ProjectSubscriptionsWrite::class.java)
            val record = findSubscriptionByName(projectEntity, name)
                ?.disabled(disabled)
                ?.toSubscriptionRecord()
                ?: throw EventSubscriptionNameNotFoundException(null, name)
            entitySubscriptionStore.save(projectEntity, record)
        } else {
            securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
            val record = globalSubscriptionStore.find(name)
                ?.let { subscriptionRecordToGlobalSubscription(it) }
                ?.disabled(disabled)
                ?.toSubscriptionRecord()
                ?: throw EventSubscriptionNameNotFoundException(null, name)
            globalSubscriptionStore.save(record)
        }
    }

    override fun filterSubscriptions(filter: EventSubscriptionFilter): PaginatedList<EventSubscription> =
        if (filter.entity == null) {
            filterGlobalSubscriptions(filter)
        } else {
            filterEntitySubscriptions(filter.entity, filter)
        }

    private fun filterGlobalSubscriptions(
        filter: EventSubscriptionFilter,
    ): PaginatedList<EventSubscription> {
        securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
        val (total, items) = filterGlobal(filter)(filter.offset, filter.size)
        return PaginatedList.create(items, filter.offset, filter.size, total)
    }

    private fun filterEntitySubscriptions(
        projectEntityID: ProjectEntityID,
        filter: EventSubscriptionFilter,
    ): PaginatedList<EventSubscription> {
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
    ): (Int, Int) -> Pair<Int, List<EventSubscription>> = { offset: Int, size: Int ->
        val (total, records) = globalSubscriptionStore.filter(filter, offset, size)
        total to records.map { subscriptionRecordToGlobalSubscription(it) }
    }

    private fun filterEntity(
        entity: ProjectEntity,
        filter: EventSubscriptionFilter,
    ): (Int, Int) -> Pair<Int, List<EventSubscription>> = { offset: Int, size: Int ->
        val (count, records) = entitySubscriptionStore.findByFilter(entity, offset, size, filter)
        count to records.map { entitySubscriptionRecordToSubscription(entity, it) }
    }

    override fun forEveryMatchingSubscription(event: Event, code: (subscription: EventSubscription) -> Unit) {

        fun matching(
            entities: Map<ProjectEntityType, ProjectEntity>,
        ) {
            if (entities.isNotEmpty()) {
                entities.values.forEach { projectEntity ->

                    entitySubscriptionStore.forEachByEvent(
                        projectEntity,
                        event,
                    ) { record ->
                        val subscription = entitySubscriptionRecordToSubscription(projectEntity, record)
                        if (event.matchesKeywords(subscription.keywords)) {
                            code(subscription)
                        }
                    }
                }
            }
        }

        // Regular entities
        matching(event.entities)
        // Extra entities
        matching(event.extraEntities)

        // Getting the global subscriptions
        globalSubscriptionStore.forEachRecord(
            context = "left join jsonb_array_elements_text(data::jsonb->'events') as events on true",
            query = "events = :event",
            queryVariables = mapOf("event" to event.eventType.id)
        ) { record ->
            val subscription = subscriptionRecordToGlobalSubscription(record)
            if (event.matchesKeywords(subscription.keywords)) {
                code(subscription)
            }
        }
    }

    override fun removeAllGlobal() {
        securityService.checkGlobalFunction(GlobalSubscriptionsManage::class.java)
        globalSubscriptionStore.deleteAll()
    }

    private fun subscriptionRecordToGlobalSubscription(
        subscriptionRecord: SubscriptionRecord
    ) = subscriptionRecord.let {
        EventSubscription(
            projectEntity = null,
            name = it.name,
            channel = it.channel,
            channelConfig = it.channelConfig,
            events = it.events.toSet(),
            keywords = it.keywords,
            disabled = it.disabled,
            origin = it.origin,
            contentTemplate = it.contentTemplate,
        )
    }

    private fun entitySubscriptionRecordToSubscription(
        projectEntity: ProjectEntity?,
        record: SubscriptionRecord,
    ) =
        EventSubscription(
            projectEntity = projectEntity,
            name = record.name,
            channel = record.channel,
            channelConfig = record.channelConfig,
            events = record.events.toSet(),
            keywords = record.keywords,
            disabled = record.disabled,
            origin = record.origin,
            contentTemplate = record.contentTemplate,
        )

}