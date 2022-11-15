package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.extension.notifications.casc.NotificationsSubCascContext
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIIgnore
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import net.nemerosa.ontrack.model.support.StorageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EntitySubscriptionsCascContext(
    private val eventSubscriptionService: EventSubscriptionService,
    private val storageService: StorageService,
    private val structureService: StructureService,
) : AbstractCascContext(), NotificationsSubCascContext {

    private val logger: Logger = LoggerFactory.getLogger(EntitySubscriptionsCascContext::class.java)

    override val field: String = "entity-subscriptions"

    override val type: CascType = cascArray(
        "List of entity-level subscriptions",
        cascObject(
            "Entity-level subscription",
            cascField(
                EntitySubscriptionCascContextData::entity,
                cascObject(EntitySubscriptionData::class)
            ),
            cascField(
                EntitySubscriptionCascContextData::subscriptions,
                cascArray(
                    "List of subscriptions for this entity",
                    cascObject(
                        "Subscription details",
                        cascField(
                            SubscriptionsCascContextData::events,
                            type = cascArray("List of event types", cascString)
                        ),
                        cascField(SubscriptionsCascContextData::keywords),
                        cascField(SubscriptionsCascContextData::channel),
                        cascField(SubscriptionsCascContextData::channelConfig),
                        cascField(SubscriptionsCascContextData::disabled),
                    )
                )
            )
        )
    )

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parse<EntitySubscriptionCascContextData>()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${EntitySubscriptionCascContextData::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }
        // Checks all incoming items
        val entities = mutableSetOf<EntitySubscriptionData>()
        val duplicatedEntities = mutableListOf<EntitySubscriptionData>()
        items.forEachIndexed { index, item ->
            checkEntity(item.entity, paths, index)
            if (entities.contains(item.entity)) {
                duplicatedEntities += item.entity
            } else {
                entities += item.entity
            }
        }
        // Checks that entities entries are not duplicated
        if (duplicatedEntities.isNotEmpty()) {
            error(
                "Duplicate entities in the notifications:\n" +
                        duplicatedEntities.joinToString("\n", prefix = " * ") {
                            it.storageKey
                        }
            )
        }
        // Gets the list of already saved items
        val existing = cachedCascEntitySubscriptions()
        // Sync between the Casc config and the actual list
        syncForward(
            from = items,
            to = existing
        ) {
            equality { a, b -> a.entity == b.entity }
            onCreation { item ->
                createSubscriptions(item)
            }
            onModification { item, existing ->
                modifySubscriptions(item, existing)
            }
            onDeletion { existing ->
                deleteSubscriptions(existing)
            }
        }
        // Saves the new items
        storageService.clear(EntitySubscriptionCascContextData::class.java.name)
        items.forEach { item ->
            storageService.store(
                EntitySubscriptionCascContextData::class.java.name,
                item.entity.storageKey,
                item,
            )
        }
    }

    private fun deleteSubscriptions(existing: EntitySubscriptionCascContextData) {
        val entity = findEntity(existing.entity)
        if (entity != null) {
            logger.debug("Deleting subscriptions for entity ${existing.entity}")
            eventSubscriptionService.deleteSubscriptionsByEntity(entity)
        } else {
            logger.info("Cannot find entity ${existing.entity}. Not deleting its subscriptions.")
        }
    }

    private fun modifySubscriptions(
        item: EntitySubscriptionCascContextData,
        existing: EntitySubscriptionCascContextData,
    ) {
        val entity = findEntity(existing.entity)
        if (entity != null) {
            logger.debug("Modifying subscriptions for entity ${existing.entity}")
            // Gets the existing subscriptions for this entity
            val entitySubscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(
                    size = Int.MAX_VALUE,
                    entity = entity.toProjectEntityID(),
                    recursive = false,
                    origin = EventSubscriptionOrigins.CASC,
                )
            ).pageItems.associate {
                SubscriptionsCascContextData(
                    events = it.data.events.toList().sorted(),
                    keywords = it.data.keywords,
                    channel = it.data.channel,
                    channelConfig = it.data.channelConfig,
                    disabled = it.data.disabled,
                ) to it.id
            }
            syncForward(
                from = item.subscriptions,
                to = existing.subscriptions
            ) {
                onCreation { item ->
                    logger.info("Subscribing to ${existing.entity}: $item")
                    subscribe(entity, item)
                }
                onModification { item, _ ->
                    // Resubscribing is enough
                    logger.info("Subscribing to ${existing.entity}: $item")
                    subscribe(entity, item)
                }
                onDeletion { cached ->
                    val existingId = entitySubscriptions[cached.normalized()]
                    if (existingId != null) {
                        eventSubscriptionService.deleteSubscriptionById(entity, existingId)
                    } else {
                        logger.info("Cannot find a subscription for ${item.entity}: not deleting it.")
                    }
                }
            }
        } else {
            logger.info("Cannot find entity ${item.entity}. Not modifying its subscriptions.")
        }
    }

    private fun createSubscriptions(item: EntitySubscriptionCascContextData) {
        val entity = findEntity(item.entity)
        if (entity != null) {
            logger.debug("Creating subscriptions for entity ${item.entity}")
            item.subscriptions.forEach { subscription ->
                logger.info("Subscribing to ${item.entity}: $subscription")
                subscribe(entity, subscription)
            }
        } else {
            logger.info("Cannot find entity ${item.entity}. Not creating the subscriptions.")
        }
    }

    private fun subscribe(
        entity: ProjectEntity,
        subscription: SubscriptionsCascContextData,
    ) {
        eventSubscriptionService.subscribe(
            EventSubscription(
                projectEntity = entity,
                events = subscription.events.toSet(),
                keywords = subscription.keywords,
                channel = subscription.channel,
                channelConfig = subscription.channelConfig,
                disabled = subscription.disabled ?: false,
                origin = EventSubscriptionOrigins.CASC,
            )
        )
    }

    private fun findEntity(entity: EntitySubscriptionData): ProjectEntity? =
        entity.findEntity(structureService)

    private fun checkEntity(entity: EntitySubscriptionData, paths: List<String>, index: Int) {
        try {
            entity.validate()
        } catch (ex: EntitySubscriptionDataValidationException) {
            error(
                """Cannot validate the entity for a subscription at${path(paths + index.toString())}: ${ex.message}"""
            )
        }
    }

    override fun render(): JsonNode = cachedCascEntitySubscriptions().asJson()

    /**
     * Gets the list of already configured Casc entity subscriptions from a stored cache
     */
    private fun cachedCascEntitySubscriptions() =
        storageService.getData(
            EntitySubscriptionCascContextData::class.java.name,
            EntitySubscriptionCascContextData::class.java
        ).values.toList()

    data class EntitySubscriptionCascContextData(
        @APIDescription("Entity to subscribe to")
        val entity: EntitySubscriptionData,
        @APIDescription("List of subscriptions for this entity")
        val subscriptions: List<SubscriptionsCascContextData>,
    )

    data class EntitySubscriptionData(
        @APIDescription("Project name")
        val project: String,
        @APIDescription("Branch name")
        val branch: String?,
        @APIDescription("Promotion level name")
        val promotion: String?,
        @APIDescription("Validation stamp name")
        val validation: String?,
    ) {
        fun validate() {
            if (!promotion.isNullOrBlank() && !validation.isNullOrBlank()) {
                throw EntitySubscriptionDataValidationException(
                    "Either promotion or validation must be set."
                )
            }
            if ((!promotion.isNullOrBlank() || !validation.isNullOrBlank()) && branch.isNullOrBlank()) {
                throw EntitySubscriptionDataValidationException(
                    "If promotion or validation is set, branch must be set as well."
                )
            }
        }

        fun findEntity(structureService: StructureService): ProjectEntity? =
            if (promotion != null) {
                structureService.findPromotionLevelByName(project, branch!!, promotion).getOrNull()
            } else if (validation != null) {
                structureService.findValidationStampByName(project, branch!!, validation).getOrNull()
            } else if (branch != null) {
                structureService.findBranchByName(project, branch).getOrNull()
            } else {
                structureService.findProjectByName(project).getOrNull()
            }

        @get:JsonIgnore
        @APIIgnore
        val storageKey: String by lazy {
            if (promotion != null) {
                "$project/$branch/pl:$promotion"
            } else if (validation != null) {
                "$project/$branch/vs:$validation"
            } else if (branch != null) {
                "$project/$branch"
            } else {
                project
            }
        }
    }

    class EntitySubscriptionDataValidationException(
        message: String,
    ) : InputException(
        message
    )
}