package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import java.util.*

/**
 * Service used to get access to the list of events.
 */
interface EventQueryService {

    fun getEvents(offset: Int, count: Int): List<Event>

    fun getEvents(entityType: ProjectEntityType, entityId: ID, offset: Int, count: Int): List<Event>

    fun getEvents(entityType: ProjectEntityType, entityId: ID, eventType: EventType, offset: Int, count: Int): List<Event>

    fun getLastEventSignature(entityType: ProjectEntityType, entityId: ID, eventType: EventType): Optional<Signature>

    fun getLastEvent(entityType: ProjectEntityType, entityId: ID, eventType: EventType): Optional<Event>

    fun getLastEvent(entity: ProjectEntity, eventType: EventType): Event?

}
