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

            writeFile(
                fileName = "index",
            ) { s ->
                s.title("List of configuration properties for Yontrack.")
                for (eventType in eventFactory.eventTypes) {
                    val id = getEventTypeFileId(eventType)
                    val name = eventType.id
                    s.tocItem(name, fileName = "${id}.md")
                }
            }

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
            title = id,
        ) { s ->

            s.paragraph(eventType.description)

            s.h2("Context")

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

            s.h2("Default template")

            s.code(eventType.template)

        }
    }

    private fun getEventTypeFileId(eventType: EventType) = "event-${eventType.id}"
}