package net.nemerosa.ontrack.docs

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

fun StringBuilder.tocItem(text: String, fileName: String) {
    appendLine("* [$text]($fileName)")
}

fun StringBuilder.table(vararg headers: String) {
    appendLine("| ${headers.joinToString(" | ")} |")
    appendLine("|${headers.joinToString("|") { "-".repeat(it.length + 2) }}|")
}

fun StringBuilder.tableRow(vararg cells: String) {
    appendLine("| ${cells.joinToString(" | ") { tableCell(it) }} |")
}

private fun tableCell(text: String) = text.replace("\n", "<br/>")
