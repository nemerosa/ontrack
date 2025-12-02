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
        fields
            .sortedBy { it.name }
            .forEach { (name, description, type, required, subfields) ->
                s.append(" ".repeat(4 * (level - 1))).append("*").append(" **").append(name).append("** - ")
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

    fun writeFile(
        fileId: String,
        title: String,
        header: String?,
        fields: List<FieldDocumentation>,
        example: String?,
        links: List<DocumentationLink> = emptyList(),
        linksPrefix: String,
        extendedHeader: (s: StringBuilder) -> Unit = {},
        extendedConfig: (s: StringBuilder) -> Unit = {},
    ) {
        writeFile(
            fileId = fileId,
            title = title,
        ) { s ->

            if (!header.isNullOrBlank()) {
                s.paragraph(header.trimIndent())
            }

            extendedHeader(s)

            if (links.isNotEmpty()) {
                s.h2("Links")
                links.forEach { link ->
                    s.listLinkItem(link.name, linksPrefix + link.value)
                }
                s.append("\n")
            }

            if (fields.isNotEmpty()) {
                s.h2("Configuration")
                writeFields(s, fields, 1)
            }

            extendedConfig(s)

            if (!example.isNullOrBlank()) {
                s.append("Example:").append("\n").append("\n")
                s.code(example)
            }

        }
    }

}
