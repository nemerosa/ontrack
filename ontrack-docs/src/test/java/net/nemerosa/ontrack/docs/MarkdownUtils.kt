package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.docs.FieldDocumentation
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
import kotlin.reflect.KClass

fun StringBuilder.text(text: String) {
    appendLine(text)
}

fun StringBuilder.paragraph(text: String) {
    appendLine()
    appendLine(text)
    appendLine()
}

fun StringBuilder.title(title: String) {
    appendLine("# $title")
    appendLine()
}

fun StringBuilder.h2(title: String) {
    appendLine()
    appendLine("## $title")
    appendLine()
}

fun StringBuilder.tocItem(text: String, fileName: String, description: String? = null) {
    appendLine("* [$text]($fileName) ${description?.takeIf { it.isNotBlank() }?.let { " - _${it}_" }}")
}

fun StringBuilder.listLinkItem(text: String, link: String) {
    appendLine("* [$text]($link)")
}

fun StringBuilder.code(code: String, language: String = "text") {
    appendLine("{% raw %}")
    appendLine("```${language}")
    appendLine(code)
    appendLine("```")
    appendLine("{% endraw %}")
}

fun StringBuilder.note(text: String) {
    appendLine("!!! note")
    appendLine("    ")
    appendLine(text.trimIndent().prependIndent("    "))
}

fun StringBuilder.table(vararg headers: String) {
    appendLine("| ${headers.joinToString(" | ")} |")
    appendLine("|${headers.joinToString("|") { "-".repeat(it.length + 2) }}|")
}

fun StringBuilder.tableRow(vararg cells: String) {
    appendLine("| ${cells.joinToString(" | ") { tableCell(it) }} |")
}

fun StringBuilder.example(type: KClass<*>) {
    val example = getDocumentationExampleCode(type)
    if (!example.isNullOrBlank()) {
        h2("Example")
        code(example.trimIndent())
    }
}

fun StringBuilder.configuration(type: KClass<*>, title: String? = "Configuration", description: String? = null) {
    if (isScalarClass(type)) {
        if (!title.isNullOrBlank()) {
            h2(title)
        }
        val s = StringBuilder(type.java.simpleName)
        if (!description.isNullOrBlank()) {
            s.append(" - ").append("_${description}_")
        }
        text(s.toString())
    } else {
        fields(type, title)
    }
}

fun StringBuilder.fields(type: KClass<*>, title: String? = "Configuration") {
    val fields = getFieldsDocumentation(type)
    if (!title.isNullOrBlank()) {
        h2(title)
    }
    writeFields(fields, 1)
}

fun StringBuilder.writeFields(
    fields: List<FieldDocumentation>,
    level: Int = 1,
) {
    fields
        .sortedBy { it.name }
        .forEach { (name, description, type, required, subfields) ->
            append(" ".repeat(4 * (level - 1))).append("*").append(" **").append(name).append("** - ")
                .append(type)
                .append(" - ")
                .append(if (required) "required" else "optional")
                .append(" - ")
                .append(description?.trimIndent()).append("\n")
                .append("\n")
            if (subfields.isNotEmpty()) {
                writeFields(subfields, level + 1)
            }
        }
}

private fun tableCell(text: String) = text.replace("\n", "<br/>")
