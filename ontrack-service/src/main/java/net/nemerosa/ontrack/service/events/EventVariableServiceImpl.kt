package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.structure.nameValues
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

    @Deprecated("Will be removed in V5. Use the new templating service instead.")
    override fun getTemplateParameters(event: Event, caseVariants: Boolean): Map<String, String> {
        val result = mutableMapOf<String, String>()
        // Entities
        event.entities.forEach { (_, entity) ->
            entity.nameValues.forEach { (name, value) ->
                putTemplateParameter(result, name, value, caseVariants)
            }
        }
        // Extra entities
        event.extraEntities.forEach { (_, entity) ->
            entity.nameValues.forEach { (name, value) ->
                putTemplateParameter(result, "extra_$name", value, caseVariants)
            }
        }
        // Values
        event.values.forEach { (_, item) ->
            putTemplateParameter(result, item.name, item.value, caseVariants)
        }
        // OK
        return result.toMap()
    }

    private fun putTemplateParameter(
        map: MutableMap<String, String>,
        name: String,
        value: String,
        caseVariants: Boolean,
    ) {
        if (caseVariants) {
            map[name.uppercase()] = value.uppercase()
            map[name.replaceFirstChar { it.titlecase() }] = value
            map[name.lowercase()] = value.lowercase()
        } else {
            map[name] = value
        }
    }

}