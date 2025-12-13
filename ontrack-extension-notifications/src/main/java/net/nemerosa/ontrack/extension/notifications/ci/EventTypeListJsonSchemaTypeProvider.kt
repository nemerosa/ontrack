package net.nemerosa.ontrack.extension.notifications.ci

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.json.schema.*
import org.springframework.stereotype.Component

@Component
class EventTypeListJsonSchemaTypeProvider(
    private val eventFactory: EventFactory,
) : JsonSchemaTypeProvider {

    override fun createType(configuration: String, jsonTypeBuilder: JsonTypeBuilder): JsonType {
        return JsonArrayType(
            description = "List of event types",
            items = JsonEnumType(
                description = "Event type",
                values = eventFactory.eventTypes.map { it.id }
            )
        )
    }

}