package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class EventQueryServiceImpl @Autowired
constructor(
        private val structureService: StructureService,
        private val securityService: SecurityService,
        private val eventFactory: EventFactory,
        private val eventRepository: EventRepository
) : EventQueryService {

    override fun getEvents(offset: Int, count: Int): List<Event> {
        // Gets the list of projects the current user is allowed to view
        val projectIds = structureService.projectList.map(Project::id)
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
        checkAccess(entityType, entityId)
        return eventRepository.query(
                entityType,
                entityId,
                offset,
                count,
                { type, id -> type.getEntityFn(structureService).apply(id) },
                { eventFactory.toEventType(it) }
        )
    }

    override fun getEvents(entityType: ProjectEntityType, entityId: ID, eventType: EventType, offset: Int, count: Int): List<Event> {
        checkAccess(entityType, entityId)
        return eventRepository.query(
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
        checkAccess(entityType, entityId)
        return eventRepository.getLastEventSignature(entityType, entityId, eventType)
    }

    private fun checkAccess(entityType: ProjectEntityType, entityId: ID) {
        securityService.checkProjectFunction(
                structureService.entityLoader().apply(
                        entityType, entityId
                ).project,
                ProjectView::class.java
        )
    }

    override fun getLastEvent(entityType: ProjectEntityType, entityId: ID, eventType: EventType): Optional<Event> {
        checkAccess(entityType, entityId)
        return eventRepository.getLastEvent(
                entityType, entityId, eventType,
                { type, id -> type.getEntityFn(structureService).apply(id) },
                { eventFactory.toEventType(it) }
        )
    }

    override fun getLastEvent(entity: ProjectEntity, eventType: EventType): Event? =
            getLastEvent(entity.projectEntityType, entity.id, eventType).orElse(null)

}
