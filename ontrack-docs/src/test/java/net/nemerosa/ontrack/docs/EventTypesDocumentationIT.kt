package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.events.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class EventTypesDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Test
    fun `Events generation`() {
        docGenSupport.inDirectory("events") {

            writeIndex(
                fileId = "appendix-event-index",
                level = 4,
                title = "List of events",
                items = eventFactory.eventTypes.associate { eventType ->
                    getEventTypeFileId(eventType) to eventType.id
                }
            )

            eventFactory.eventTypes.forEach { eventType ->
                generateEventType(this, eventType)
            }
        }
    }

    private fun generateEventType(directoryContext: DocGenDirectoryContext, eventType: EventType) {
        val id = eventType.id

        val fileId = getEventTypeFileId(eventType)

        directoryContext.writeFile(
            fileId = fileId,
            level = 5,
            title = id,
        ) { s ->

            s.append(eventType.description).append("\n\n")

            s.append("Context:\n\n")

            eventType.context.items.forEach { (name, item) ->
                s.append("* ")
                s.append("`$name` - ")
                when (item) {
                    is EventTypeContextEntity -> s.append(item.projectEntityType.displayName)
                    is EventTypeContextAnyEntity -> s.append("any entity")
                    is EventTypeContextValue -> s.append("string")
                }
                s.append(" - ").append(item.description).append("\n")
            }

            s.append("\nDefault template:\n\n")
            s.append("[source]\n")
            s.append("----\n")
            s.append(eventType.template)
            s.append("\n----\n\n")

        }
    }

    private fun getEventTypeFileId(eventType: EventType) = "event-${eventType.id}"
}