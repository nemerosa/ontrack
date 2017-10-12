package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class EventQueryServiceImpl @Autowired
constructor(private val structureService: StructureService, private val eventFactory: EventFactory, private val eventRepository: EventRepository) : EventQueryService {

    override fun getEvents(offset: Int, count: Int): List<Event> {
        // Gets the list of projects the current user is allowed to view
        val projectIds = allowedProjectIds
        // Performs the query
        return eventRepository.query(
                projectIds,
                offset,
                count,
                { type, id -> type.getEntityFn(structureService).apply(id) },
                { eventFactory.toEventType(it) }
        )
    }

    override fun getEvents(entityType: ProjectEntityType, entityId: ID, offset: Int, count: Int): List<Event> {
        // Gets the list of projects the current user is allowed to view
        val projectIds = allowedProjectIds
        // Performs the query
        return eventRepository.query(
                projectIds,
                entityType,
                entityId,
                offset,
                count,
                { type, id -> type.getEntityFn(structureService).apply(id) },
                { eventFactory.toEventType(it) }
        )
    }

    private val allowedProjectIds: List<Int>
        get() = structureService.projectList.map(Project::id)

    override fun getEvents(entityType: ProjectEntityType, entityId: ID, eventType: EventType, offset: Int, count: Int): List<Event> {
        return eventRepository.query(
                allowedProjectIds,
                eventType,
                entityType,
                entityId,
                offset,
                count,
                { type, id -> type.getEntityFn(structureService).apply(id) },
                { eventFactory.toEventType(it) }
        )
    }

    override fun getLastEventSignature(entityType: ProjectEntityType, entityId: ID, eventType: EventType): Optional<Signature> {
        return eventRepository.getLastEventSignature(entityType, entityId, eventType)
    }

    override fun getLastEvent(entityType: ProjectEntityType, entityId: ID, eventType: EventType): Optional<Event> {
        return eventRepository.getLastEvent(
                entityType, entityId, eventType,
                { type, id -> type.getEntityFn(structureService).apply(id) },
                { eventFactory.toEventType(it) }
        )
    }

    override fun getLastEvent(entity: ProjectEntity, eventType: EventType): Event? =
            getLastEvent(entity.projectEntityType, entity.id, eventType).orElse(null)

}
