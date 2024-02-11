package net.nemerosa.ontrack.boot.docs

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
import net.nemerosa.ontrack.model.events.*
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.model.templating.TemplatingSource
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.File

/**
 * Generation of the documentation
 */
class DocumentationGenerationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var templatingFunctions: List<TemplatingFunction>

    @Autowired
    private lateinit var templatingFilters: List<TemplatingFilter>

    @Autowired
    private lateinit var templatingSources: List<TemplatingSource>

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Test
    fun `Templating functions generation`() {
        withDirectory("templating/functions") {

            writeIndex(
                fileId = "appendix-templating-functions-index",
                level = 4,
                title = "List of templating functions",
                items = templatingFunctions.associate { templatingFunction ->
                    getTemplatingFunctionFileId(templatingFunction) to templatingFunction.id
                }
            )

            templatingFunctions.forEach { templatingFunction ->
                generateTemplatingFunction(this, templatingFunction)
            }
        }
    }

    @Test
    fun `Templating filters generation`() {
        withDirectory("templating/filters") {

            writeIndex(
                fileId = "appendix-templating-filters-index",
                level = 4,
                title = "List of templating filters",
                items = templatingFilters.associate { templatingFilter ->
                    getTemplatingFilterFileId(templatingFilter) to templatingFilter.id
                }
            )

            templatingFilters.forEach { templatingFilter ->
                generateTemplatingFilter(this, templatingFilter)
            }
        }
    }

    @Test
    fun `Templating sources generation`() {
        withDirectory("templating/sources") {

            writeIndex(
                fileId = "appendix-templating-sources-index",
                level = 4,
                title = "List of templating sources",
                items = templatingSources.associate { templatingSource ->
                    getTemplatingSourceFileId(templatingSource) to templatingSource.field
                }
            )

            templatingSources.forEach { templatingSource ->
                generateTemplatingSource(this, templatingSource)
            }
        }
    }

    @Test
    fun `Events generation`() {
        withDirectory("events") {

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

    private fun generateEventType(directoryContext: DirectoryContext, eventType: EventType) {
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

    private fun generateTemplatingFunction(directoryContext: DirectoryContext, templatingFunction: TemplatingFunction) {
        val id = templatingFunction.id
        val description = getAPITypeDescription(templatingFunction::class)
        val parameters = getFieldsDocumentation(templatingFunction::class)
        val example = getDocumentationExampleCode(templatingFunction::class)

        val fileId = getTemplatingFunctionFileId(templatingFunction)

        directoryContext.writeFile(
            fileId = fileId,
            level = 5,
            title = id,
            header = description,
            fields = parameters,
            example = example,
        )
    }

    private fun getTemplatingFunctionFileId(templatingFunction: TemplatingFunction) =
        "templating-function-${templatingFunction.id}"

    private fun generateTemplatingFilter(directoryContext: DirectoryContext, templatingFilter: TemplatingFilter) {
        val id = templatingFilter.id
        val description = getAPITypeDescription(templatingFilter::class)
        val example = getDocumentationExampleCode(templatingFilter::class)

        val fileId = getTemplatingFilterFileId(templatingFilter)

        directoryContext.writeFile(
            fileId = fileId,
            level = 5,
            title = id,
            header = description,
            fields = emptyMap(),
            example = example,
        )
    }

    private fun getTemplatingFilterFileId(templatingFilter: TemplatingFilter) =
        "templating-filter-${templatingFilter.id}"

    private fun generateTemplatingSource(directoryContext: DirectoryContext, templatingSource: TemplatingSource) {
        val field = templatingSource.field
        val description = getAPITypeDescription(templatingSource::class)
        val parameters = getFieldsDocumentation(templatingSource::class)
        val example = getDocumentationExampleCode(templatingSource::class)
        val types = templatingSource.types

        val fileId = getTemplatingSourceFileId(templatingSource)

        directoryContext.writeFile(
            fileId = fileId,
            level = 5,
            title = field,
            header = description,
            fields = parameters,
            example = example,
        ) { s ->
            s.append("Applicable for:\n\n")
            types.forEach { type ->
                s.append("* ").append(type.displayName).append("\n")
            }
            s.append("\n")
        }
    }

    private fun getTemplatingSourceFileId(templatingSource: TemplatingSource) =
        "templating-source-${templatingSource.field}"

    private class DirectoryContext(
        val dir: File,
    ) {

        fun writeFile(
            fileId: String,
            fileName: String = fileId,
            level: Int,
            title: String,
            code: (s: StringBuilder) -> Unit,
        ) {
            val file = File(dir, "${fileName}.adoc")

            val s = StringBuilder()

            val fileTitle = "${(1..level).joinToString("") { "=" }} $title"

            s.append("[[").append(fileId).append("]]\n")
            s.append(fileTitle).append("\n").append("\n")

            code(s)

            file.writeText(s.toString())
        }

        fun writeIndex(
            fileId: String,
            level: Int,
            title: String,
            items: Map<String, String>,
        ) {
            writeFile(
                fileId = fileId,
                fileName = "index",
                level = level,
                title = title,
            ) { s ->
                val sortedItems = items.toSortedMap()
                sortedItems.forEach { (id, title) ->
                    s.append("* ").append("<<$id,$title>>\n")
                }
                s.append("\n")
                sortedItems.forEach { (id, _) ->
                    s.append("include::$id.adoc[]\n")
                }
            }
        }

        fun writeFile(
            fileId: String,
            level: Int,
            title: String,
            header: String?,
            fields: Map<String, String>,
            example: String?,
            extendedHeader: (s: StringBuilder) -> Unit = {},
        ) {
            writeFile(
                fileId = fileId,
                level = level,
                title = title,
            ) { s ->

                if (!header.isNullOrBlank()) {
                    s.append(header.trimIndent()).append("\n").append("\n")
                }

                extendedHeader(s)

                if (fields.isNotEmpty()) {
                    s.append("Configuration:\n\n")
                    fields.toSortedMap().forEach { (name, description) ->
                        s.append("* **").append(name).append("** - ").append(description.trimIndent()).append("\n")
                            .append("\n")
                    }
                }

                if (!example.isNullOrBlank()) {
                    s.append("Example:").append("\n").append("\n")
                    s.append("[source]\n----\n")
                    s.append(example)
                    s.append("\n----\n")
                }

            }
        }
    }

    private fun withDirectory(path: String, code: DirectoryContext.() -> Unit) {
        val root = File("../ontrack-docs/src/docs/asciidoc")
        val dir = File(root, path)
        if (!dir.exists()) {
            dir.mkdirs()
        } else {
            dir.listFiles()?.forEach { file ->
                file.delete()
            }
        }
        val context = DirectoryContext(dir)
        context.code()
    }

}