package net.nemerosa.ontrack.extension.issues.export

/**
 * Definition of an export format for the issues.
 */
@Deprecated("Will be removed in V5. Use the templating service")
data class ExportFormat(
        val id: String,
        val name: String,
        val type: String
) {

    companion object {
        @JvmField
        val TEXT = ExportFormat("text", "Text", "text/plain")
        @JvmField
        val MARKDOWN = ExportFormat("markdown", "Markdown", "text/plain")
        @JvmField
        val SLACK = ExportFormat("slack", "Slack", "text/plain")
        @JvmField
        val HTML = ExportFormat("html", "HTML", "text/html")
    }

}
