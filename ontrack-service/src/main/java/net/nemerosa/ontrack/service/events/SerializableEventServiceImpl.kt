package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SerializableEventServiceImpl(
    private val eventFactory: EventFactory,
    private val structureService: StructureService,
) : SerializableEventService {

    override fun dehydrate(event: Event): SerializableEvent = event.dehydrate()

    override fun hydrate(serialized: SerializableEvent) = serialized.run {
        Event(
            eventType = eventFactory.toEventType(eventType),
            signature = signature,
            entities = entities.mapValues { (entityType, entityId) ->
                entityType.getFindEntityFn(structureService).apply(ID.of(entityId))
            },
            extraEntities = extraEntities.mapValues { (entityType, entityId) ->
                entityType.getFindEntityFn(structureService).apply(ID.of(entityId))
            },
            ref = ref,
            values = values,
        )
    }

}