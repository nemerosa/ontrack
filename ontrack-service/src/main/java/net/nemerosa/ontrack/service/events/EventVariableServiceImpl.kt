package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.structure.varName
import org.springframework.stereotype.Service

@Service
class EventVariableServiceImpl : EventVariableService {

    override fun getTemplateContext(event: Event, context: Map<String, Any>): Map<String, Any> {
        val result = context.toMutableMap()
        // Entities
        event.entities.forEach { (type, entity) ->
            result[type.varName] = entity
        }
        // Extra entities
        event.extraEntities.forEach { (type, entity) ->
            result["x${type.varName.replaceFirstChar { it.uppercase() }}"] = entity
        }
        // Ref entity
        val ref = event.ref
        if (ref != null) {
            val refEntity = event.entities[ref]
            if (refEntity != null) {
                result["entity"] = refEntity
            }
        }
        // Values
        event.values.forEach { (name, item) ->
            result[name] = item.value
            result["${name}_NAME"] = item.name
        }
        // OK
        return result.toMap()
    }

}