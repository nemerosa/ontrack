package net.nemerosa.ontrack.extension.issues.export

/**
 * List of issues, exported as text for a given format.
 */
data class ExportedIssues(
        val format: String,
        val content: String
)
