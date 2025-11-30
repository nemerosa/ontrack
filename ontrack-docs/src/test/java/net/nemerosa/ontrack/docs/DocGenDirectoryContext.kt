package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.docs.DocumentationLink
import net.nemerosa.ontrack.model.docs.FieldDocumentation
import java.io.File

class DocGenDirectoryContext(
    private val dir: File,
) {

    fun writeFields(
        s: StringBuilder,
        fields: List<FieldDocumentation>,
        level: Int = 1,
    ) {
        fields.sortedBy { it.name }.forEach { (name, description, type, required, subfields) ->
            s.append("*".repeat(level)).append(" **").append(name).append("** - ")
                .append(type)
                .append(" - ")
                .append(if (required) "required" else "optional")
                .append(" - ")
                .append(description?.trimIndent()).append("\n")
                .append("\n")
            if (subfields.isNotEmpty()) {
                writeFields(s, subfields, level + 1)
            }
        }
    }

    fun writeFile(
        fileName: String,
        code: (s: StringBuilder) -> Unit,
    ) {
        val file = File(dir, "${fileName}.md")

        val s = StringBuilder()
        code(s)

        file.writeText(s.toString())
    }

    @Deprecated("Use Markdown")
    fun writeFile(
        fileId: String,
        fileName: String = fileId,
        level: Int,
        title: String,
        code: (s: StringBuilder) -> Unit,
    ) {
        writeFile(
            fileName = fileName,
        ) { s ->
            val fileTitle = "${(1..level).joinToString("") { "=" }} $title"

            s.append("[[").append(fileId).append("]]\n")
            s.append(fileTitle).append("\n").append("\n")

            code(s)
        }
    }

    fun writeFile(
        fileId: String,
        fileName: String = fileId,
        title: String,
        code: (s: StringBuilder) -> Unit,
    ) {
        writeFile(
            fileName = fileName,
        ) { s ->
            s.title(title)
            code(s)
        }
    }

    @Deprecated("Use inline generation of links for Markdown")
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
        fields: List<FieldDocumentation>,
        example: String?,
        links: List<DocumentationLink> = emptyList(),
        extendedHeader: (s: StringBuilder) -> Unit = {},
        extendedConfig: (s: StringBuilder) -> Unit = {},
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

            if (links.isNotEmpty()) {
                s.append("Links:\n\n")
                links.forEach { link ->
                    s.append("* <<").append(link.value).append(",").append(link.name).append(">>\n")
                }
                s.append("\n")
            }

            if (fields.isNotEmpty()) {
                s.append("Configuration:\n\n")
                writeFields(s, fields, 1)
            }

            extendedConfig(s)

            if (!example.isNullOrBlank()) {
                s.append("Example:").append("\n").append("\n")
                s.append("[source]\n----\n")
                s.append(example)
                s.append("\n----\n")
            }

        }
    }

}
