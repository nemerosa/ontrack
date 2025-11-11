package net.nemerosa.ontrack.docs

//import net.nemerosa.ontrack.extension.notifications.channels.NoTemplate
//import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
//import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
//import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
//import net.nemerosa.ontrack.model.docs.DocumentationIgnore
//import net.nemerosa.ontrack.model.docs.DocumentationQualifier
//import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
//import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
//import net.nemerosa.ontrack.model.events.*
//import net.nemerosa.ontrack.model.templating.TemplatingFilter
//import net.nemerosa.ontrack.model.templating.TemplatingFunction
//import net.nemerosa.ontrack.model.templating.TemplatingRenderableDoc
//import net.nemerosa.ontrack.model.templating.TemplatingSource
//import org.junit.jupiter.api.Disabled
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import kotlin.reflect.full.findAnnotation
//import kotlin.reflect.full.findAnnotations
//import kotlin.reflect.full.hasAnnotation
//import kotlin.test.fail

/**
 * Generation of the documentation
 */
//@Disabled("To be launched manually when need be")
class DocumentationGenerationIT : AbstractDocumentationGenerationTestSupport() {
//
//    @Autowired
//    private lateinit var templatingFilters: List<TemplatingFilter>
//
//    @Autowired
//    private lateinit var eventFactory: EventFactory
//
//    @Test
//    fun `Templating filters generation`() {
//        withDirectory("templating/filters") {
//
//            writeIndex(
//                fileId = "appendix-templating-filters-index",
//                level = 4,
//                title = "List of templating filters",
//                items = templatingFilters.associate { templatingFilter ->
//                    getTemplatingFilterFileId(templatingFilter) to templatingFilter.id
//                }
//            )
//
//            templatingFilters.forEach { templatingFilter ->
//                generateTemplatingFilter(this, templatingFilter)
//            }
//        }
//    }
//
//    @Test
//    fun `Events generation`() {
//        withDirectory("events") {
//
//            writeIndex(
//                fileId = "appendix-event-index",
//                level = 4,
//                title = "List of events",
//                items = eventFactory.eventTypes.associate { eventType ->
//                    getEventTypeFileId(eventType) to eventType.id
//                }
//            )
//
//            eventFactory.eventTypes.forEach { eventType ->
//                generateEventType(this, eventType)
//            }
//        }
//    }
//
//    private fun generateEventType(directoryContext: DirectoryContext, eventType: EventType) {
//        val id = eventType.id
//
//        val fileId = getEventTypeFileId(eventType)
//
//        directoryContext.writeFile(
//            fileId = fileId,
//            level = 5,
//            title = id,
//        ) { s ->
//
//            s.append(eventType.description).append("\n\n")
//
//            s.append("Context:\n\n")
//
//            eventType.context.items.forEach { (name, item) ->
//                s.append("* ")
//                s.append("`$name` - ")
//                when (item) {
//                    is EventTypeContextEntity -> s.append(item.projectEntityType.displayName)
//                    is EventTypeContextAnyEntity -> s.append("any entity")
//                    is EventTypeContextValue -> s.append("string")
//                }
//                s.append(" - ").append(item.description).append("\n")
//            }
//
//            s.append("\nDefault template:\n\n")
//            s.append("[source]\n")
//            s.append("----\n")
//            s.append(eventType.template)
//            s.append("\n----\n\n")
//
//        }
//    }
//
//    private fun getEventTypeFileId(eventType: EventType) = "event-${eventType.id}"
//
//
//    private fun generateTemplatingFilter(directoryContext: DirectoryContext, templatingFilter: TemplatingFilter) {
//        val id = templatingFilter.id
//        val description = getAPITypeDescription(templatingFilter::class)
//        val example = getDocumentationExampleCode(templatingFilter::class)
//
//        val fileId = getTemplatingFilterFileId(templatingFilter)
//
//        directoryContext.writeFile(
//            fileId = fileId,
//            level = 5,
//            title = id,
//            header = description,
//            fields = emptyList(),
//            example = example,
//        )
//    }
//
//    private fun getTemplatingFilterFileId(templatingFilter: TemplatingFilter) =
//        "templating-filter-${templatingFilter.id}"

}