package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.extension.api.EventParameterExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.nameValues
import net.nemerosa.ontrack.model.structure.varName
import org.springframework.stereotype.Service

@Service
class EventVariableServiceImpl(
    private val extensionManager: ExtensionManager,
) : EventVariableService {

    private val eventParameterExtensions: Collection<EventParameterExtension> by lazy {
        extensionManager.getExtensions(EventParameterExtension::class.java)
    }

    override fun getTemplateContext(event: Event): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        // Entities
        event.entities.forEach { (type, entity) ->
            result[type.varName] = entity
        }
        // Extra entities
        event.extraEntities.forEach { (type, entity) ->
            result["x${type.varName.replaceFirstChar { it.uppercase() }}"] = entity
        }
        // Values
        event.values.forEach { (_, item) ->
            result[item.name] = item.value
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
            // Derivative values for this entity
            putDerivateValues(entity, result, caseVariants)
        }
        // Extra entities
        event.extraEntities.forEach { (_, entity) ->
            entity.nameValues.forEach { (name, value) ->
                putTemplateParameter(result, "extra_$name", value, caseVariants)
            }
            // Derivative values for this entity
            putDerivateValues(entity, result, caseVariants)
        }
        // Values
        event.values.forEach { (_, item) ->
            putTemplateParameter(result, item.name, item.value, caseVariants)
        }
        // OK
        return result.toMap()
    }

    private fun putDerivateValues(
        entity: ProjectEntity,
        result: MutableMap<String, String>,
        caseVariants: Boolean
    ) {
        eventParameterExtensions.forEach { eventParameterExtension ->
            val additionalParameters = eventParameterExtension.additionalTemplateParameters(entity)
            additionalParameters.forEach { (name, value) ->
                putTemplateParameter(result, name, value, caseVariants)
            }
        }
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