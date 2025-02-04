package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.NameValue

/**
 * Serializable event
 */
data class AsyncEventListeningQueueEvent(
    val accountName: String,
    val eventType: String,
    val signature: Signature?,
    val entities: Map<ProjectEntityType, Int>,
    val extraEntities: Map<ProjectEntityType, Int>,
    val ref: ProjectEntityType?,
    val values: Map<String, NameValue>,
) {
    constructor(accountName: String, event: Event) : this(
        accountName = accountName,
        eventType = event.eventType.id,
        signature = event.signature,
        entities = event.entities.mapValues { (_, entity) -> entity.id() },
        extraEntities = event.extraEntities.mapValues { (_, entity) -> entity.id() },
        ref = event.ref,
        values = event.values,
    )

    fun toEvent(
        eventFactory: EventFactory,
        structureService: StructureService,
    ) = Event(
        eventFactory.toEventType(eventType),
        signature,
        entities.mapValues { (type, id) -> type.getEntityFn(structureService).apply(ID.of(id)) },
        extraEntities.mapValues { (type, id) -> type.getEntityFn(structureService).apply(ID.of(id)) },
        ref,
        values,
    )
}
