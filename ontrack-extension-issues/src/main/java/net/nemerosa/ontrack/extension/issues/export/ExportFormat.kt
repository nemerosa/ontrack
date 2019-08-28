package net.nemerosa.ontrack.extension.issues.export

/**
 * Definition of an export format for the issues.
 */
class ExportFormat(
        val id: String,
        val name: String,
        val type: String
) {

    companion object {
        @JvmStatic
        val TEXT = ExportFormat("text", "Text", "text/plain")
        @JvmStatic
        val MARKDOWN = ExportFormat("markdown", "Markdown", "text/plain")
        @JvmStatic
        val HTML = ExportFormat("html", "HTML", "text/html")
    }

}
